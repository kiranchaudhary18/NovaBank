import { createFileRoute } from "@tanstack/react-router";
import { motion } from "framer-motion";
import { useCurrentUser } from "@/lib/api/hooks/use-auth";
import { User, Bell, Lock, Globe, Key, Trash2 } from "lucide-react";
import { useState } from "react";

export const Route = createFileRoute("/_authenticated/settings")({
  component: SettingsPage,
});

const tabs = [
  { id: "profile", label: "Profile", icon: User },
  { id: "notifications", label: "Notifications", icon: Bell },
  { id: "security", label: "Security", icon: Lock },
  { id: "preferences", label: "Preferences", icon: Globe },
  { id: "api", label: "API Keys", icon: Key },
];

function SettingsPage() {
  const [activeTab, setActiveTab] = useState("profile");
  const { data: user } = useCurrentUser();

  return (
    <div className="p-6 lg:p-8 max-w-7xl mx-auto space-y-8">
      <motion.div
        initial={{ opacity: 0, y: 10 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.4 }}
      >
        <h2 className="text-2xl sm:text-3xl font-display font-bold text-white tracking-tight">
          Settings
        </h2>
        <p className="text-sm text-muted-foreground mt-1">
          Manage your account settings and preferences.
        </p>
      </motion.div>

      <div className="flex flex-col md:flex-row gap-8">
        <aside className="w-full md:w-64 shrink-0">
          <nav className="flex md:flex-col gap-2 overflow-x-auto pb-4 md:pb-0 scrollbar-none">
            {tabs.map((tab) => {
              const isActive = activeTab === tab.id;
              return (
                <button
                  key={tab.id}
                  onClick={() => setActiveTab(tab.id)}
                  className={`flex items-center gap-3 px-4 py-2.5 rounded-xl text-sm font-medium transition-all duration-200 whitespace-nowrap ${
                    isActive
                      ? "bg-primary/10 text-primary border border-primary/20"
                      : "text-muted-foreground hover:bg-white/5 hover:text-white border border-transparent"
                  }`}
                >
                  <tab.icon className="w-4 h-4" />
                  {tab.label}
                </button>
              );
            })}
          </nav>
        </aside>

        <main className="flex-1 max-w-3xl">
          <motion.div
            key={activeTab}
            initial={{ opacity: 0, x: 20 }}
            animate={{ opacity: 1, x: 0 }}
            transition={{ duration: 0.4 }}
            className="space-y-8"
          >
            {activeTab === "profile" && (
              <div className="space-y-6">
                <div className="rounded-2xl border glass p-6">
                  <h3 className="text-lg font-semibold text-white mb-6">Personal Information</h3>

                  <div className="flex items-center gap-6 mb-8">
                    <div className="relative">
                      <div className="w-24 h-24 rounded-full border border-white/10 bg-primary/20 flex items-center justify-center text-primary text-3xl font-bold">
                        {user?.fullName?.charAt(0).toUpperCase() || "U"}
                      </div>
                      <button className="absolute bottom-0 right-0 w-8 h-8 rounded-full bg-white/10 border border-white/20 flex items-center justify-center text-white hover:bg-white/20 transition-colors">
                        <User className="w-4 h-4" />
                      </button>
                    </div>
                    <div>
                      <button className="px-4 py-2 rounded-lg bg-white/5 border border-white/10 text-sm font-medium text-white hover:bg-white/10 transition-colors">
                        Change Avatar
                      </button>
                      <p className="text-xs text-muted-foreground mt-2">
                        JPG, GIF or PNG. 1MB max.
                      </p>
                    </div>
                  </div>

                  <div className="grid grid-cols-1 sm:grid-cols-2 gap-6">
                    <div className="space-y-2">
                      <label className="text-sm font-medium text-white">Full Name</label>
                      <input
                        type="text"
                        defaultValue={user?.fullName || ""}
                        className="w-full bg-white/5 border border-white/10 rounded-lg px-4 py-2 text-white outline-none focus:border-primary/50"
                      />
                    </div>
                    <div className="space-y-2">
                      <label className="text-sm font-medium text-white">Email Address</label>
                      <input
                        type="email"
                        defaultValue={user?.email || ""}
                        className="w-full bg-white/5 border border-white/10 rounded-lg px-4 py-2 text-white outline-none focus:border-primary/50"
                      />
                    </div>
                  </div>

                  <div className="mt-6 flex justify-end">
                    <button className="px-6 py-2 rounded-xl bg-gradient-primary text-white font-semibold shadow-glow hover:opacity-95 transition-opacity">
                      Save Changes
                    </button>
                  </div>
                </div>

                <div className="rounded-2xl border border-red-500/20 bg-red-500/[0.02] p-6 backdrop-blur-md">
                  <h3 className="text-lg font-semibold text-red-400 mb-2 flex items-center gap-2">
                    <Trash2 className="w-5 h-5" /> Danger Zone
                  </h3>
                  <p className="text-sm text-muted-foreground mb-4">
                    Permanently remove your personal account and all of its contents from the
                    NovaBank platform.
                  </p>
                  <button className="px-4 py-2 rounded-lg bg-red-500/10 border border-red-500/20 text-sm font-medium text-red-400 hover:bg-red-500/20 transition-colors">
                    Delete Account
                  </button>
                </div>
              </div>
            )}

            {activeTab !== "profile" && (
              <div className="rounded-2xl border glass p-6 h-[400px] flex flex-col items-center justify-center text-center">
                <div className="w-16 h-16 rounded-full bg-white/5 flex items-center justify-center mb-4">
                  <Lock className="w-8 h-8 text-muted-foreground" />
                </div>
                <h3 className="text-xl font-semibold text-white mb-2">Under Construction</h3>
                <p className="text-muted-foreground">
                  This settings panel is being built by the NovaBank engineering team.
                </p>
              </div>
            )}
          </motion.div>
        </main>
      </div>
    </div>
  );
}
