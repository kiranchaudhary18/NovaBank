import { createFileRoute } from "@tanstack/react-router";
import { motion } from "framer-motion";
import { ShieldCheck, Key, Smartphone, History, ShieldAlert } from "lucide-react";

export const Route = createFileRoute("/_authenticated/security")({
  component: SecurityPage,
});

function SecurityPage() {
  return (
    <div className="p-6 lg:p-8 max-w-7xl mx-auto space-y-8">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 mb-8">
        <motion.div
          initial={{ opacity: 0, y: 10 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.4 }}
        >
          <h2 className="text-2xl sm:text-3xl font-display font-bold text-white tracking-tight">
            Security & Access
          </h2>
          <p className="text-sm text-muted-foreground mt-1">
            Manage your password, 2FA, and authorized devices.
          </p>
        </motion.div>
      </div>

      <div className="grid lg:grid-cols-2 gap-6">
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5, delay: 0.1 }}
          className="space-y-6"
        >
          {/* 2FA */}
          <div className="rounded-2xl border glass p-6">
            <div className="flex items-start gap-4">
              <div className="p-3 rounded-xl bg-primary/20 text-primary border border-primary/30">
                <Smartphone className="w-6 h-6" />
              </div>
              <div className="flex-1">
                <div className="flex justify-between items-start">
                  <div>
                    <h3 className="text-lg font-semibold text-white">Two-Factor Authentication</h3>
                    <p className="text-sm text-muted-foreground mt-1 mb-4">
                      Add an extra layer of security to your account.
                    </p>
                  </div>
                  <div className="px-2.5 py-1 rounded-full bg-green-500/10 border border-green-500/20 text-xs font-semibold text-green-400">
                    Enabled
                  </div>
                </div>
                <button className="px-4 py-2 rounded-lg bg-white/5 border border-white/10 text-sm font-medium text-white hover:bg-white/10 transition-colors">
                  Manage 2FA
                </button>
              </div>
            </div>
          </div>

          {/* Password */}
          <div className="rounded-2xl border glass p-6">
            <div className="flex items-start gap-4">
              <div className="p-3 rounded-xl bg-white/5 text-muted-foreground border border-white/10">
                <Key className="w-6 h-6" />
              </div>
              <div className="flex-1">
                <h3 className="text-lg font-semibold text-white">Password</h3>
                <p className="text-sm text-muted-foreground mt-1 mb-4">
                  Last changed 3 months ago. We recommend changing it periodically.
                </p>
                <button className="px-4 py-2 rounded-lg bg-white/5 border border-white/10 text-sm font-medium text-white hover:bg-white/10 transition-colors">
                  Change Password
                </button>
              </div>
            </div>
          </div>
        </motion.div>

        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5, delay: 0.2 }}
          className="space-y-6"
        >
          {/* Sessions */}
          <div className="rounded-2xl border glass p-6">
            <div className="flex items-center gap-2 mb-6">
              <History className="w-5 h-5 text-white" />
              <h3 className="text-lg font-semibold text-white">Active Sessions</h3>
            </div>

            <div className="space-y-4">
              <div className="flex items-center justify-between p-4 rounded-xl bg-white/5 border border-white/10">
                <div>
                  <h4 className="font-medium text-white">MacBook Pro (Chrome)</h4>
                  <p className="text-xs text-muted-foreground mt-1">
                    San Francisco, CA • Current Session
                  </p>
                </div>
                <div className="px-2 py-1 rounded bg-primary/20 text-xs font-semibold text-primary">
                  Active
                </div>
              </div>

              <div className="flex items-center justify-between p-4 rounded-xl bg-white/5 border border-white/10">
                <div>
                  <h4 className="font-medium text-white">iPhone 14 Pro (App)</h4>
                  <p className="text-xs text-muted-foreground mt-1">
                    San Jose, CA • Last active 2 hours ago
                  </p>
                </div>
                <button className="text-xs font-semibold text-red-400 hover:text-red-300">
                  Revoke
                </button>
              </div>
            </div>
          </div>

          <div className="rounded-2xl border border-red-500/20 bg-red-500/[0.02] p-6 backdrop-blur-md flex items-start gap-4">
            <div className="p-3 rounded-full bg-red-500/10 text-red-400 shrink-0">
              <ShieldAlert className="w-6 h-6" />
            </div>
            <div>
              <h4 className="font-semibold text-red-400">Suspicious Activity?</h4>
              <p className="text-sm text-muted-foreground mt-1 mb-3">
                If you notice anything unusual, you can instantly lock your account and freeze all
                cards.
              </p>
              <button className="px-4 py-2 rounded-lg bg-red-500/10 border border-red-500/20 text-sm font-medium text-red-400 hover:bg-red-500/20 transition-colors">
                Lock Account
              </button>
            </div>
          </div>
        </motion.div>
      </div>
    </div>
  );
}
