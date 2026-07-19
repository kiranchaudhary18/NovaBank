import { createFileRoute } from "@tanstack/react-router";
import { motion } from "framer-motion";
import { AccountService } from "@/lib/api/services/account.service";
import { useQuery } from "@tanstack/react-query";
import { Copy, Snowflake, Download, Eye, ArrowRightLeft } from "lucide-react";
import { Link } from "@tanstack/react-router";
import { toast } from "sonner";

export const Route = createFileRoute("/_authenticated/accounts")({
  component: AccountsPage,
});

function AccountsPage() {
  const { data: accounts = [], isLoading } = useQuery({
    queryKey: ["accounts"],
    queryFn: () => AccountService.getMyAccounts(),
    staleTime: 5 * 60 * 1000, // 5 minutes cache to prevent constant reloading
  });

  if (isLoading) {
    return (
      <div className="p-6 lg:p-8 flex items-center justify-center min-h-[400px]">
        <div className="h-8 w-8 animate-spin rounded-full border-4 border-primary border-t-transparent" />
      </div>
    );
  }

  return (
    <div className="p-6 lg:p-8 max-w-7xl mx-auto space-y-8">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <motion.div
          initial={{ opacity: 0, y: 10 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.4 }}
        >
          <h2 className="text-2xl sm:text-3xl font-display font-bold text-white tracking-tight">
            Your Accounts
          </h2>
          <p className="text-sm text-muted-foreground mt-1">
            Manage your corporate savings, checking, and operating accounts.
          </p>
        </motion.div>

        <motion.div
          initial={{ opacity: 0, scale: 0.95 }}
          animate={{ opacity: 1, scale: 1 }}
          transition={{ duration: 0.4, delay: 0.1 }}
        >
          <button 
            onClick={() => toast("Account Opening", { description: "New account requests must be processed in-branch. Please contact support to initiate the process." })}
            className="rounded-xl bg-gradient-primary px-4 py-2 text-sm font-semibold text-white shadow-glow hover:opacity-95 transition-opacity"
          >
            + Open New Account
          </button>
        </motion.div>
      </div>

      <div className="grid lg:grid-cols-2 gap-6">
        {accounts.map((acc, index) => (
          <motion.div
            key={acc.id}
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.5, delay: index * 0.1 }}
            className="group relative overflow-hidden rounded-3xl border glass p-8 transition-colors hover:bg-white/[0.04]"
          >
            {/* Background Gradient Blob */}
            <div className="absolute -right-20 -top-20 h-64 w-64 rounded-full bg-primary/20 blur-[100px] pointer-events-none group-hover:bg-primary/30 transition-all duration-500" />

            <div className="relative z-10">
              <div className="flex justify-between items-start mb-12">
                <div>
                  <div className="inline-flex items-center rounded-full bg-white/5 border border-white/10 px-2.5 py-0.5 text-xs font-semibold text-white mb-4">
                    {acc.type} Account
                  </div>
                  <h3 className="text-2xl font-semibold text-white">{acc.name}</h3>
                </div>
                <div className="text-right">
                  <p className="text-sm font-medium text-muted-foreground mb-1">
                    Available Balance
                  </p>
                  <p className="text-3xl font-display font-bold text-white tracking-tight">
                    {acc.currency === "USD" ? "$" : acc.currency === "EUR" ? "€" : ""}
                    {acc.balance.toLocaleString("en-US", { minimumFractionDigits: 2 })}
                  </p>
                </div>
              </div>

              <div className="flex items-center gap-4 mb-8">
                <div className="flex-1 rounded-xl bg-white/5 border border-white/10 p-3 font-mono text-sm text-white/80 tracking-widest flex items-center justify-between">
                  {acc.accountNumber}
                  <button onClick={() => toast.info('Feature Coming Soon', { description: 'This module is currently in development.' })}  className="text-muted-foreground hover:text-white transition-colors">
                    <Copy className="h-4 w-4" />
                  </button>
                </div>
                {acc.interestRate && (
                  <div className="rounded-xl bg-green-500/10 border border-green-500/20 px-4 py-3 text-sm font-semibold text-green-400">
                    {acc.interestRate} APY
                  </div>
                )}
              </div>

              <div className="flex flex-wrap items-center gap-3 pt-6 border-t border-white/10">
                <Link to="/transfer" className="flex items-center gap-2 rounded-lg bg-white/5 px-4 py-2 text-sm font-medium text-white transition-colors hover:bg-white/10">
                  <ArrowRightLeft className="h-4 w-4 text-muted-foreground" />
                  Transfer
                </Link>
                <Link to="/transactions" className="flex items-center gap-2 rounded-lg bg-white/5 px-4 py-2 text-sm font-medium text-white transition-colors hover:bg-white/10">
                  <Eye className="h-4 w-4 text-muted-foreground" />
                  View Details
                </Link>
                <div className="flex-1" />
                <button onClick={() => toast.info('Feature Coming Soon', { description: 'This module is currently in development.' })}  className="flex items-center gap-2 rounded-lg bg-white/5 px-3 py-2 text-sm font-medium text-white transition-colors hover:bg-white/10 group-hover:text-primary">
                  <Download className="h-4 w-4" />
                </button>
                <button onClick={() => toast.info('Feature Coming Soon', { description: 'This module is currently in development.' })}  className="flex items-center gap-2 rounded-lg bg-white/5 px-3 py-2 text-sm font-medium text-white transition-colors hover:bg-red-500/20 hover:text-red-400 group-hover:text-red-400/80">
                  <Snowflake className="h-4 w-4" />
                </button>
              </div>
            </div>
          </motion.div>
        ))}
      </div>
    </div>
  );
}
