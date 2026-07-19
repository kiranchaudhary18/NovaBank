import { createFileRoute } from "@tanstack/react-router";
import { motion } from "framer-motion";
import { Landmark, ArrowRight, Clock, ShieldCheck, Briefcase } from "lucide-react";

export const Route = createFileRoute("/_authenticated/loans")({
  component: LoansPage,
});

function LoansPage() {
  return (
    <div className="p-6 lg:p-8 max-w-7xl mx-auto space-y-8">
      <motion.div
        initial={{ opacity: 0, y: 10 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.4 }}
      >
        <h2 className="text-2xl sm:text-3xl font-display font-bold text-white tracking-tight">
          Corporate Loans
        </h2>
        <p className="text-sm text-muted-foreground mt-1">
          Access flexible capital for your business growth.
        </p>
      </motion.div>

      <div className="flex flex-col items-center justify-center py-20 text-center glass rounded-3xl border border-white/10">
        <div className="w-16 h-16 rounded-full bg-white/5 flex items-center justify-center mb-4">
          <Briefcase className="w-8 h-8 text-muted-foreground" />
        </div>
        <h3 className="text-xl font-semibold text-white mb-2">No active loans</h3>
        <p className="text-muted-foreground max-w-sm mb-6">
          You currently don't have any active loans or financing. 
          Grow your business with our tailored financial products.
        </p>
        <button className="flex items-center gap-2 rounded-xl bg-gradient-primary px-6 py-2.5 text-sm font-semibold text-white shadow-glow hover:opacity-95 transition-opacity">
          Apply for a Loan
        </button>
      </div>

      <div className="grid lg:grid-cols-3 gap-6">
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5, delay: 0.1 }}
          className="lg:col-span-2 rounded-3xl border glass p-8 relative overflow-hidden"
        >
          <div className="absolute top-0 right-0 p-8 opacity-10">
            <Landmark className="w-48 h-48 text-primary" />
          </div>

          <div className="relative z-10 max-w-lg">
            <div className="inline-flex items-center rounded-full bg-primary/20 border border-primary/30 px-3 py-1 text-xs font-semibold text-primary mb-6">
              Pre-approved Offer
            </div>
            <h3 className="text-4xl font-display font-bold text-white mb-4">
              Get up to $500,000 in working capital.
            </h3>
            <p className="text-muted-foreground mb-8 text-lg">
              Based on your revenue history, you're pre-approved for a dynamic credit line with
              rates starting at 4.2% APR.
            </p>

            <div className="flex items-center gap-4">
              <button className="flex items-center gap-2 rounded-xl bg-gradient-primary px-6 py-3 font-semibold text-white shadow-glow hover:opacity-95 transition-all">
                Review Offer <ArrowRight className="w-4 h-4" />
              </button>
            </div>
          </div>
        </motion.div>

        <motion.div
          initial={{ opacity: 0, x: 20 }}
          animate={{ opacity: 1, x: 0 }}
          transition={{ duration: 0.5, delay: 0.2 }}
          className="space-y-6"
        >
          <div className="rounded-3xl border glass p-6">
            <h4 className="font-semibold text-white mb-4">Active Loans</h4>

            <div className="p-4 rounded-xl bg-white/5 border border-white/10">
              <div className="flex justify-between items-start mb-2">
                <div>
                  <h5 className="font-medium text-white">Equipment Financing</h5>
                  <p className="text-xs text-muted-foreground">Term Loan • 4.5% APR</p>
                </div>
                <div className="text-right">
                  <p className="font-semibold text-white">$45,200</p>
                  <p className="text-xs text-muted-foreground">Remaining</p>
                </div>
              </div>

              <div className="mt-4 pt-4 border-t border-white/10 flex items-center justify-between text-sm">
                <span className="flex items-center gap-1.5 text-muted-foreground">
                  <Clock className="w-4 h-4" /> Next EMI: Aug 15
                </span>
                <span className="font-medium text-white">$1,250</span>
              </div>
            </div>
          </div>

          <div className="rounded-3xl border glass p-6 flex items-start gap-4">
            <div className="p-3 rounded-full bg-green-500/20 text-green-400 shrink-0">
              <ShieldCheck className="w-6 h-6" />
            </div>
            <div>
              <h4 className="font-semibold text-white">Excellent Credit</h4>
              <p className="text-sm text-muted-foreground mt-1">
                Your business credit score is 780. You qualify for premium rates.
              </p>
            </div>
          </div>
        </motion.div>
      </div>
    </div>
  );
}
