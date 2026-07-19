import { createFileRoute } from "@tanstack/react-router";
import { toast } from "sonner";
import { motion } from "framer-motion";
import { useQuery } from "@tanstack/react-query";
import { NotificationService } from "@/lib/api/services/notification.service";
import { Bell, Shield, ArrowDownRight } from "lucide-react";

export const Route = createFileRoute("/_authenticated/notifications")({
  component: NotificationsPage,
});

function NotificationsPage() {
  const { data: notificationPage, isLoading } = useQuery({
    queryKey: ["notifications"],
    queryFn: () => NotificationService.getNotifications(),
  });

  const notificationsList = notificationPage?.content || [];

  return (
    <div className="p-6 lg:p-8 max-w-4xl mx-auto space-y-8">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 mb-8">
        <motion.div
          initial={{ opacity: 0, y: 10 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.4 }}
        >
          <h2 className="text-2xl sm:text-3xl font-display font-bold text-white tracking-tight">
            Notifications
          </h2>
          <p className="text-sm text-muted-foreground mt-1">
            Stay updated with alerts, transfers, and security notices.
          </p>
        </motion.div>

        <motion.div
          initial={{ opacity: 0, scale: 0.95 }}
          animate={{ opacity: 1, scale: 1 }}
          transition={{ duration: 0.4, delay: 0.1 }}
        >
          <button onClick={() => toast.info('Feature Coming Soon', { description: 'This module is currently in development.' })}  className="text-sm font-semibold text-primary hover:text-primary-light transition-colors">
            Mark all as read
          </button>
        </motion.div>
      </div>

      {isLoading ? (
        <div className="flex items-center justify-center min-h-[300px]">
          <div className="h-8 w-8 animate-spin rounded-full border-4 border-primary border-t-transparent" />
        </div>
      ) : notificationsList.length === 0 ? (
        <div className="flex flex-col items-center justify-center py-20 text-center glass rounded-2xl border">
          <div className="w-16 h-16 rounded-full bg-white/5 flex items-center justify-center mb-4">
            <Bell className="w-8 h-8 text-muted-foreground" />
          </div>
          <h3 className="text-xl font-semibold text-white mb-2">No notifications yet</h3>
          <p className="text-muted-foreground max-w-sm">
            You're all caught up! We'll let you know when something needs your attention.
          </p>
        </div>
      ) : (
        <div className="space-y-4">
          {notificationsList.map((notif, idx) => (
            <motion.div
              key={notif.id}
              initial={{ opacity: 0, x: -20 }}
              animate={{ opacity: 1, x: 0 }}
              transition={{ duration: 0.4, delay: idx * 0.1 }}
              className={`flex gap-4 p-6 rounded-2xl border transition-colors cursor-pointer group ${
                !notif.isRead
                  ? "glass border-primary/30 bg-primary/[0.02]"
                  : "glass border-white/5 hover:bg-white/[0.02]"
              }`}
            >
              <div
                className={`w-12 h-12 rounded-xl flex items-center justify-center shrink-0 border ${
                  notif.type === "ALERT" || notif.type === "SECURITY"
                    ? "bg-red-500/10 text-red-400 border-red-500/20"
                    : notif.type === "TRANSACTION"
                      ? "bg-green-500/10 text-green-400 border-green-500/20"
                      : "bg-primary/10 text-primary border-primary/20"
                }`}
              >
                {notif.type === "ALERT" || notif.type === "SECURITY" ? (
                  <Shield className="w-5 h-5" />
                ) : notif.type === "TRANSACTION" ? (
                  <ArrowDownRight className="w-5 h-5" />
                ) : (
                  <Bell className="w-5 h-5" />
                )}
              </div>

              <div className="flex-1">
                <div className="flex justify-between items-start mb-1">
                  <h3
                    className={`font-semibold ${!notif.isRead ? "text-white" : "text-white/80 group-hover:text-white"} transition-colors`}
                  >
                    {notif.title}
                  </h3>
                  <span className="text-xs font-medium text-muted-foreground whitespace-nowrap ml-4">
                    {new Date(notif.createdAt).toLocaleDateString()}
                  </span>
                </div>
                <p className="text-sm text-muted-foreground">{notif.message}</p>
              </div>

              {!notif.isRead && (
                <div className="w-2 h-2 rounded-full bg-primary mt-2 shrink-0 shadow-glow" />
              )}
            </motion.div>
          ))}
        </div>
      )}
    </div>
  );
}
