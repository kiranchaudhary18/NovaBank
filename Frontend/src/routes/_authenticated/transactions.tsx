import { createFileRoute } from "@tanstack/react-router";
import { motion } from "framer-motion";
import { Search, Download, Filter, FileText } from "lucide-react";
import { useQuery } from "@tanstack/react-query";
import { AccountService } from "@/lib/api/services/account.service";
import { TransactionService } from "@/lib/api/services/transaction.service";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { Badge } from "@/components/ui/badge";

export const Route = createFileRoute("/_authenticated/transactions")({
  component: TransactionsPage,
});

function TransactionsPage() {
  const { data: accounts = [] } = useQuery({
    queryKey: ["accounts"],
    queryFn: () => AccountService.getMyAccounts(),
  });

  const primaryAccount = accounts[0]?.accountNumber;

  const { data: transactions = [], isLoading } = useQuery({
    queryKey: ["transactions", primaryAccount],
    queryFn: () => TransactionService.getTransactions(primaryAccount as string),
    enabled: !!primaryAccount,
  });

  return (
    <div className="p-6 lg:p-8 max-w-7xl mx-auto space-y-8">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <motion.div
          initial={{ opacity: 0, y: 10 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.4 }}
        >
          <h2 className="text-2xl sm:text-3xl font-display font-bold text-white tracking-tight">
            Transactions
          </h2>
          <p className="text-sm text-muted-foreground mt-1">
            View and manage all your corporate transactions in one place.
          </p>
        </motion.div>

        <motion.div
          initial={{ opacity: 0, scale: 0.95 }}
          animate={{ opacity: 1, scale: 1 }}
          transition={{ duration: 0.4, delay: 0.1 }}
          className="flex items-center gap-3"
        >
          <button className="flex items-center gap-2 rounded-xl bg-white/5 border border-white/10 px-4 py-2 text-sm font-semibold text-white hover:bg-white/10 transition-colors">
            <Download className="h-4 w-4" />
            Export CSV
          </button>
          <button className="flex items-center gap-2 rounded-xl bg-white/5 border border-white/10 px-4 py-2 text-sm font-semibold text-white hover:bg-white/10 transition-colors">
            <FileText className="h-4 w-4" />
            Export PDF
          </button>
        </motion.div>
      </div>

      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5, delay: 0.2 }}
        className="rounded-2xl border glass overflow-hidden flex flex-col"
      >
        <div className="p-4 border-b border-white/10 flex flex-col sm:flex-row items-center justify-between gap-4">
          <div className="relative w-full sm:max-w-xs">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
            <input
              type="text"
              placeholder="Search transactions..."
              className="w-full bg-white/5 border border-white/10 rounded-lg pl-10 pr-4 py-2 text-sm text-white placeholder:text-muted-foreground focus:outline-none focus:border-primary/50 transition-colors"
            />
          </div>
          <div className="flex items-center gap-3 w-full sm:w-auto">
            <button className="flex-1 sm:flex-none flex items-center justify-center gap-2 rounded-lg bg-white/5 border border-white/10 px-4 py-2 text-sm font-medium text-white hover:bg-white/10 transition-colors">
              <Filter className="h-4 w-4" />
              Status
            </button>
            <button className="flex-1 sm:flex-none flex items-center justify-center gap-2 rounded-lg bg-white/5 border border-white/10 px-4 py-2 text-sm font-medium text-white hover:bg-white/10 transition-colors">
              <Filter className="h-4 w-4" />
              Category
            </button>
          </div>
        </div>

        <div className="w-full overflow-auto min-h-[300px]">
          {isLoading ? (
            <div className="flex items-center justify-center h-full pt-12">
              <div className="h-8 w-8 animate-spin rounded-full border-4 border-primary border-t-transparent" />
            </div>
          ) : (
            <Table>
              <TableHeader>
                <TableRow className="border-b border-white/5 hover:bg-transparent">
                  <TableHead className="text-muted-foreground font-medium w-[250px]">
                    Description
                  </TableHead>
                  <TableHead className="text-muted-foreground font-medium">Category</TableHead>
                  <TableHead className="text-muted-foreground font-medium">Date</TableHead>
                  <TableHead className="text-muted-foreground font-medium">Status</TableHead>
                  <TableHead className="text-muted-foreground font-medium text-right">
                    Amount
                  </TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {transactions.map((tx) => (
                  <TableRow
                    key={tx.id}
                    className="border-b border-white/5 hover:bg-white/5 transition-colors cursor-pointer group"
                  >
                    <TableCell>
                      <div className="flex items-center gap-3">
                        <div className="h-10 w-10 rounded-full flex items-center justify-center bg-white/5 p-2 shrink-0 border border-white/10">
                          {tx.merchant?.[0]?.toUpperCase() || "T"}
                        </div>
                        <span className="font-medium text-white group-hover:text-primary transition-colors">
                          {tx.merchant}
                        </span>
                      </div>
                    </TableCell>
                    <TableCell className="text-muted-foreground">
                      <span className="inline-flex items-center rounded-full bg-white/5 px-2.5 py-0.5 text-xs font-medium text-white/80">
                        {tx.category}
                      </span>
                    </TableCell>
                    <TableCell className="text-muted-foreground">
                      {new Date(tx.createdAt).toLocaleDateString("en-US", {
                        month: "short",
                        day: "numeric",
                        year: "numeric",
                        hour: "2-digit",
                        minute: "2-digit",
                      })}
                    </TableCell>
                    <TableCell>
                      <Badge
                        variant="outline"
                        className={
                          tx.status === "SUCCESS"
                            ? "bg-green-500/10 text-green-400 border-green-500/20"
                            : "bg-yellow-500/10 text-yellow-400 border-yellow-500/20"
                        }
                      >
                        {tx.status}
                      </Badge>
                    </TableCell>
                    <TableCell
                      className={`text-right font-semibold ${tx.type === "CREDIT" ? "text-green-400" : "text-white"}`}
                    >
                      {tx.type === "CREDIT" ? "+" : "-"}
                      {tx.amount.toLocaleString("en-US", { style: "currency", currency: "USD" })}
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          )}
        </div>

        <div className="p-4 border-t border-white/10 flex items-center justify-between text-sm text-muted-foreground">
          <span>Showing 1 to {transactions.length} of {transactions.length} transactions</span>
          <div className="flex items-center gap-2">
            <button
              className="px-3 py-1 rounded-md hover:bg-white/5 transition-colors disabled:opacity-50"
              disabled
            >
              Previous
            </button>
            <button className="px-3 py-1 rounded-md bg-white/10 text-white">1</button>
            <button
              className="px-3 py-1 rounded-md hover:bg-white/5 transition-colors disabled:opacity-50"
              disabled
            >
              Next
            </button>
          </div>
        </div>
      </motion.div>
    </div>
  );
}
