import { createFileRoute } from "@tanstack/react-router";
import { motion } from "framer-motion";
import { TrendingUp, ArrowUpRight, ArrowDownRight, Activity } from "lucide-react";

export const Route = createFileRoute("/_authenticated/investments")({
  component: InvestmentsPage,
});



function InvestmentsPage() {
  return (
    <div className="p-6 lg:p-8 max-w-7xl mx-auto space-y-8">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 mb-8">
        <motion.div
          initial={{ opacity: 0, y: 10 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.4 }}
        >
          <h2 className="text-2xl sm:text-3xl font-display font-bold text-white tracking-tight">
            Treasury & Investments
          </h2>
          <p className="text-sm text-muted-foreground mt-1">
            Grow your idle cash with premium investment vehicles.
          </p>
        </motion.div>

        <motion.div
          initial={{ opacity: 0, scale: 0.95 }}
          animate={{ opacity: 1, scale: 1 }}
          transition={{ duration: 0.4, delay: 0.1 }}
        >
          <button className="rounded-xl bg-gradient-primary px-4 py-2 text-sm font-semibold text-white shadow-glow hover:opacity-95 transition-opacity">
            Trade Assets
          </button>
        </motion.div>
      </div>

      <div className="flex flex-col items-center justify-center py-20 text-center glass rounded-3xl border border-white/10">
        <div className="w-16 h-16 rounded-full bg-white/5 flex items-center justify-center mb-4">
          <TrendingUp className="w-8 h-8 text-muted-foreground" />
        </div>
        <h3 className="text-xl font-semibold text-white mb-2">No active investments</h3>
        <p className="text-muted-foreground max-w-sm mb-6">
          Your investment portfolio is currently empty. Start trading assets to grow your wealth.
        </p>
        <button className="flex items-center gap-2 rounded-xl bg-gradient-primary px-6 py-2.5 text-sm font-semibold text-white shadow-glow hover:opacity-95 transition-opacity">
          Explore Markets
        </button>
      </div>
    </div>
  );
}
