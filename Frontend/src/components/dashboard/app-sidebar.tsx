import {
  LayoutDashboard,
  WalletCards,
  ArrowRightLeft,
  Send,
  Users,
  CreditCard,
  Landmark,
  TrendingUp,
  Receipt,
  UserSquare2,
  BarChart3,
  FileText,
  Bell,
  Sparkles,
  ShieldCheck,
  Settings,
  LogOut,
} from "lucide-react";
import { Link, useLocation } from "@tanstack/react-router";
import { cn } from "@/lib/utils";
import { NovaLogo } from "@/components/brand/logo";

const menuItems = [
  {
    group: "Core",
    items: [
      { name: "Dashboard", icon: LayoutDashboard, path: "/dashboard" },
      { name: "Accounts", icon: WalletCards, path: "/accounts" },
      { name: "Transactions", icon: ArrowRightLeft, path: "/transactions" },
    ],
  },
  {
    group: "Payments",
    items: [
      { name: "Transfer Money", icon: Send, path: "/transfer" },
      { name: "Beneficiaries", icon: Users, path: "/beneficiaries" },
      { name: "Payments", icon: Receipt, path: "/payments" },
    ],
  },
  {
    group: "Products",
    items: [
      { name: "Cards", icon: CreditCard, path: "/cards" },
      { name: "Loans", icon: Landmark, path: "/loans" },
      { name: "Investments", icon: TrendingUp, path: "/investments" },
    ],
  },
  {
    group: "Management",
    items: [
      { name: "Customers", icon: UserSquare2, path: "/customers" },
      { name: "Analytics", icon: BarChart3, path: "/analytics" },
      { name: "Reports", icon: FileText, path: "/reports" },
    ],
  },
];

const bottomItems = [
  { name: "AI Assistant", icon: Sparkles, path: "/ai-assistant", highlight: true },
  { name: "Notifications", icon: Bell, path: "/notifications" },
  { name: "Security", icon: ShieldCheck, path: "/security" },
  { name: "Settings", icon: Settings, path: "/settings" },
];

export function AppSidebar() {
  const location = useLocation();
  const currentPath = location.pathname;

  return (
    <aside className="sticky top-0 h-screen w-64 border-r border-white/5 glass-strong flex flex-col hidden lg:flex shrink-0 relative z-20">
      <div className="flex h-16 items-center px-6 border-b border-white/5">
        <NovaLogo />
      </div>

      <div className="flex-1 overflow-y-auto scrollbar-none py-6 px-4 space-y-8">
        {menuItems.map((group) => (
          <div key={group.group}>
            <div className="mb-3 px-2 text-xs font-semibold tracking-wider text-muted-foreground uppercase">
              {group.group}
            </div>
            <div className="space-y-1">
              {group.items.map((item) => {
                const isActive = currentPath.startsWith(item.path);
                return (
                  <Link
                    key={item.path}
                    to={item.path}
                    className={cn(
                      "flex items-center gap-3 rounded-xl px-3 py-2 text-sm font-medium transition-all duration-200",
                      isActive
                        ? "bg-primary/10 text-primary"
                        : "text-muted-foreground hover:bg-white/5 hover:text-white",
                    )}
                  >
                    <item.icon className={cn("h-4 w-4", isActive ? "text-primary" : "")} />
                    {item.name}
                  </Link>
                );
              })}
            </div>
          </div>
        ))}
      </div>

      <div className="border-t border-white/5 p-4 space-y-1 bg-background/30">
        {bottomItems.map((item) => {
          const isActive = currentPath.startsWith(item.path);
          return (
            <Link
              key={item.path}
              to={item.path}
              className={cn(
                "flex items-center gap-3 rounded-xl px-3 py-2 text-sm font-medium transition-all duration-200",
                isActive
                  ? "bg-primary/10 text-primary"
                  : "text-muted-foreground hover:bg-white/5 hover:text-white",
                item.highlight &&
                  !isActive &&
                  "text-primary/80 hover:text-primary hover:bg-primary/10",
              )}
            >
              <item.icon className="h-4 w-4" />
              {item.name}
            </Link>
          );
        })}
        <Link
          to="/"
          className="flex items-center gap-3 rounded-xl px-3 py-2 text-sm font-medium text-red-400/80 hover:bg-red-500/10 hover:text-red-400 transition-all duration-200 mt-2"
        >
          <LogOut className="h-4 w-4" />
          Logout
        </Link>
      </div>
    </aside>
  );
}
