import { createFileRoute } from "@tanstack/react-router";
import { toast } from "sonner";
import { motion } from "framer-motion";
import { Receipt, Download, Filter } from "lucide-react";
import { useState } from "react";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
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

export const Route = createFileRoute("/_authenticated/payments")({
  component: PaymentsPage,
});

function PaymentsPage() {
  const { data: accounts = [] } = useQuery({
    queryKey: ["accounts"],
    queryFn: () => AccountService.getMyAccounts(),
  });

  const primaryAccount = accounts[0]?.accountNumber;

  const { data: transactions = [] } = useQuery({
    queryKey: ["transactions", primaryAccount],
    queryFn: () => TransactionService.getTransactions(primaryAccount as string),
    enabled: !!primaryAccount,
  });

  const [localPayments, setLocalPayments] = useState<any[]>([]);
  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const [vendor, setVendor] = useState("");
  const [category, setCategory] = useState("");
  const [amount, setAmount] = useState("");

  const handlePay = (e: React.FormEvent) => {
    e.preventDefault();
    if (!vendor || !amount) return;
    const newPayment = {
      id: Math.random().toString(36).substr(2, 9),
      merchant: vendor,
      category: category || "Business Services",
      createdAt: new Date().toISOString(),
      amount: parseFloat(amount),
      type: "DEBIT"
    };
    setLocalPayments([newPayment, ...localPayments]);
    setIsDialogOpen(false);
    toast.success("Bill payment scheduled successfully!");
    setVendor("");
    setCategory("");
    setAmount("");
  };

  const payments = [...localPayments, ...transactions.filter((t) => t.type === "DEBIT")];

  return (
    <div className="p-6 lg:p-8 max-w-7xl mx-auto space-y-8">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 mb-8">
        <motion.div
          initial={{ opacity: 0, y: 10 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.4 }}
        >
          <h2 className="text-2xl sm:text-3xl font-display font-bold text-white tracking-tight">
            Accounts Payable
          </h2>
          <p className="text-sm text-muted-foreground mt-1">
            Manage your scheduled payments, bills, and vendor invoices.
          </p>
        </motion.div>

        <motion.div
          initial={{ opacity: 0, scale: 0.95 }}
          animate={{ opacity: 1, scale: 1 }}
          transition={{ duration: 0.4, delay: 0.1 }}
          className="flex items-center gap-3"
        >
          <button onClick={() => toast.info('Feature Coming Soon', { description: 'This module is currently in development.' })}  className="flex items-center gap-2 rounded-xl bg-white/5 border border-white/10 px-4 py-2 text-sm font-semibold text-white hover:bg-white/10 transition-colors">
            <Filter className="w-4 h-4" />
            Filter
          </button>
          <Dialog open={isDialogOpen} onOpenChange={setIsDialogOpen}>
            <DialogTrigger asChild>
              <button className="flex items-center gap-2 rounded-xl bg-gradient-primary px-4 py-2 text-sm font-semibold text-white shadow-glow hover:opacity-95 transition-opacity">
                <Receipt className="w-4 h-4" />
                Pay Bill
              </button>
            </DialogTrigger>
            <DialogContent className="glass border-white/10 text-white sm:max-w-md">
              <DialogHeader>
                <DialogTitle>Pay Bill</DialogTitle>
              </DialogHeader>
              <form onSubmit={handlePay} className="space-y-4 pt-4">
                <div className="space-y-2">
                  <label className="text-sm font-medium text-white/70">Vendor Name</label>
                  <input 
                    type="text" 
                    value={vendor} 
                    onChange={(e) => setVendor(e.target.value)}
                    className="w-full rounded-lg bg-white/5 border border-white/10 px-3 py-2 text-white outline-none focus:border-primary/50 transition-colors" 
                    placeholder="Enter vendor name" 
                    required
                  />
                </div>
                <div className="space-y-2">
                  <label className="text-sm font-medium text-white/70">Category</label>
                  <input 
                    type="text" 
                    value={category} 
                    onChange={(e) => setCategory(e.target.value)}
                    className="w-full rounded-lg bg-white/5 border border-white/10 px-3 py-2 text-white outline-none focus:border-primary/50 transition-colors" 
                    placeholder="e.g. Software, Utilities" 
                  />
                </div>
                <div className="space-y-2">
                  <label className="text-sm font-medium text-white/70">Amount (USD)</label>
                  <input 
                    type="number" 
                    value={amount} 
                    onChange={(e) => setAmount(e.target.value)}
                    className="w-full rounded-lg bg-white/5 border border-white/10 px-3 py-2 text-white outline-none focus:border-primary/50 transition-colors" 
                    placeholder="Enter amount" 
                    min="1"
                    required
                  />
                </div>
                <button type="submit" className="w-full rounded-lg bg-gradient-primary py-2.5 font-semibold text-white hover:opacity-95 transition-opacity">
                  Schedule Payment
                </button>
              </form>
            </DialogContent>
          </Dialog>
        </motion.div>
      </div>

      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5, delay: 0.2 }}
        className="rounded-2xl border glass overflow-hidden"
      >
        <div className="w-full overflow-auto">
          <Table>
            <TableHeader>
              <TableRow className="border-b border-white/5 hover:bg-transparent">
                <TableHead className="text-muted-foreground font-medium w-[250px]">
                  Vendor
                </TableHead>
                <TableHead className="text-muted-foreground font-medium">Category</TableHead>
                <TableHead className="text-muted-foreground font-medium">Due Date</TableHead>
                <TableHead className="text-muted-foreground font-medium">Status</TableHead>
                <TableHead className="text-muted-foreground font-medium text-right">
                  Amount
                </TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {payments.map((tx) => (
                <TableRow
                  key={tx.id}
                  className="border-b border-white/5 hover:bg-white/5 transition-colors cursor-pointer group"
                >
                  <TableCell>
                    <div className="flex items-center gap-3">
                      <div className="h-10 w-10 flex items-center justify-center rounded-full bg-white/5 p-2 shrink-0 border border-white/10 text-white font-semibold">
                        {tx.merchant?.[0]?.toUpperCase() || "V"}
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
                    })}
                  </TableCell>
                  <TableCell>
                    <Badge
                      variant="outline"
                      className="bg-yellow-500/10 text-yellow-400 border-yellow-500/20"
                    >
                      Pending
                    </Badge>
                  </TableCell>
                  <TableCell className="text-right font-semibold text-white">
                    ${tx.amount.toLocaleString("en-US", { minimumFractionDigits: 2 })}
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </div>
      </motion.div>
    </div>
  );
}
