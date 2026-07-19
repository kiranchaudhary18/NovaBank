import { createFileRoute } from "@tanstack/react-router";
import { toast } from "sonner";
import { motion } from "framer-motion";
import { Search, UserPlus, Star, MoreVertical } from "lucide-react";
import { useQuery } from "@tanstack/react-query";
import { BeneficiaryService } from "@/lib/api/services/beneficiary.service";

export const Route = createFileRoute("/_authenticated/beneficiaries")({
  component: BeneficiariesPage,
});

function BeneficiariesPage() {
  const { data: beneficiariesPage, isLoading } = useQuery({
    queryKey: ["beneficiaries"],
    queryFn: () => BeneficiaryService.searchBeneficiaries(),
  });

  const beneficiaries = beneficiariesPage?.content || [];

  return (
    <div className="p-6 lg:p-8 max-w-7xl mx-auto space-y-8">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 mb-8">
        <motion.div
          initial={{ opacity: 0, y: 10 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.4 }}
        >
          <h2 className="text-2xl sm:text-3xl font-display font-bold text-white tracking-tight">
            Beneficiaries
          </h2>
          <p className="text-sm text-muted-foreground mt-1">
            Manage your saved accounts for quick transfers.
          </p>
        </motion.div>

        <motion.div
          initial={{ opacity: 0, scale: 0.95 }}
          animate={{ opacity: 1, scale: 1 }}
          transition={{ duration: 0.4, delay: 0.1 }}
          className="flex items-center gap-4"
        >
          <div className="relative hidden sm:block">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
            <input
              type="text"
              placeholder="Search beneficiaries..."
              className="w-64 bg-white/5 border border-white/10 rounded-xl pl-10 pr-4 py-2 text-sm text-white placeholder:text-muted-foreground focus:outline-none focus:border-primary/50 transition-colors"
            />
          </div>
          <button onClick={() => toast.info('Feature Coming Soon', { description: 'This module is currently in development.' })}  className="flex items-center gap-2 rounded-xl bg-gradient-primary px-4 py-2 text-sm font-semibold text-white shadow-glow hover:opacity-95 transition-opacity">
            <UserPlus className="w-4 h-4" />
            Add New
          </button>
        </motion.div>
      </div>

      {isLoading ? (
        <div className="flex items-center justify-center min-h-[300px]">
          <div className="h-8 w-8 animate-spin rounded-full border-4 border-primary border-t-transparent" />
        </div>
      ) : beneficiaries.length === 0 ? (
        <div className="flex flex-col items-center justify-center py-20 text-center glass rounded-2xl border">
          <div className="w-16 h-16 rounded-full bg-white/5 flex items-center justify-center mb-4">
            <UserPlus className="w-8 h-8 text-muted-foreground" />
          </div>
          <h3 className="text-xl font-semibold text-white mb-2">No beneficiaries yet</h3>
          <p className="text-muted-foreground max-w-sm mb-6">
            Add a beneficiary to start making quick transfers.
          </p>
          <button onClick={() => toast.info('Feature Coming Soon', { description: 'This module is currently in development.' })}  className="flex items-center gap-2 rounded-xl bg-gradient-primary px-6 py-2.5 text-sm font-semibold text-white shadow-glow hover:opacity-95 transition-opacity">
            <UserPlus className="w-4 h-4" />
            Add First Beneficiary
          </button>
        </div>
      ) : (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
          {beneficiaries.map((ben, idx) => (
            <motion.div
              key={ben.id}
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.4, delay: idx * 0.05 }}
              className="group rounded-2xl border glass p-6 hover:bg-white/[0.04] transition-colors relative"
            >
              <button onClick={() => toast.info('Feature Coming Soon', { description: 'This module is currently in development.' })}  className="absolute top-4 right-4 text-muted-foreground hover:text-white transition-colors">
                <MoreVertical className="w-5 h-5" />
              </button>

              <div className="flex flex-col items-center text-center space-y-4">
                <div className="relative">
                  <div className="w-16 h-16 rounded-full bg-primary/20 text-primary flex items-center justify-center text-xl font-bold font-display border border-primary/30">
                    {ben.nickname?.substring(0, 2).toUpperCase() || ben.name.substring(0, 2).toUpperCase()}
                  </div>
                  {ben.isFavorite && (
                    <div className="absolute -bottom-1 -right-1 w-6 h-6 rounded-full bg-yellow-500/20 flex items-center justify-center border border-yellow-500/30">
                      <Star className="w-3 h-3 text-yellow-400 fill-yellow-400" />
                    </div>
                  )}
                </div>

                <div>
                  <h3 className="font-semibold text-white">{ben.nickname || ben.name}</h3>
                  <p className="text-sm text-muted-foreground mt-1">{ben.bankName}</p>
                  <p className="text-xs font-mono text-white/50 mt-1">••• {ben.accountNumber.slice(-4)}</p>
                </div>

                <div className="w-full pt-4 border-t border-white/10 flex gap-2">
                  <button onClick={() => toast.info('Feature Coming Soon', { description: 'This module is currently in development.' })}  className="flex-1 py-2 rounded-lg bg-white/5 text-sm font-medium text-white hover:bg-white/10 transition-colors">
                    Details
                  </button>
                  <button onClick={() => toast.info('Feature Coming Soon', { description: 'This module is currently in development.' })}  className="flex-1 py-2 rounded-lg bg-primary/10 text-sm font-medium text-primary hover:bg-primary/20 transition-colors">
                    Send
                  </button>
                </div>
              </div>
            </motion.div>
          ))}
        </div>
      )}
    </div>
  );
}
