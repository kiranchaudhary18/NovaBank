import { Search, Bell, Sparkles, ChevronDown } from "lucide-react";
import { Link, useLocation } from "@tanstack/react-router";
import { useCurrentUser } from "@/lib/api/hooks/use-auth";

export function TopNav() {
  const location = useLocation();
  const currentPath = location.pathname;
  const { data: user } = useCurrentUser();

  // Very simple breadcrumb generation
  const pageName =
    currentPath === "/"
      ? "Dashboard"
      : currentPath
          .split("/")
          .filter(Boolean)
          .map((segment) => segment.charAt(0).toUpperCase() + segment.slice(1))
          .join(" / ");

  return (
    <header className="sticky top-0 z-40 flex h-16 shrink-0 items-center gap-x-4 border-b border-white/5 glass px-4 sm:gap-x-6 sm:px-6 lg:px-8">
      <div className="flex flex-1 items-center gap-x-4 self-stretch lg:gap-x-6">
        <div className="flex flex-1">
          <h1 className="text-sm font-semibold leading-6 text-white hidden sm:block">{pageName}</h1>
        </div>
        <div className="flex items-center gap-x-4 lg:gap-x-6">
          <button
            type="button"
            className="flex items-center justify-between w-64 rounded-xl border border-white/10 bg-white/5 px-3 py-1.5 text-sm text-muted-foreground transition-colors hover:bg-white/10 hover:text-white"
          >
            <span className="flex items-center gap-2">
              <Search className="h-4 w-4" />
              <span>Search...</span>
            </span>
            <kbd className="hidden sm:inline-block rounded border border-white/20 bg-white/10 px-1.5 font-mono text-[10px] font-medium text-white/70">
              <span className="text-xs">⌘</span>K
            </kbd>
          </button>

          <div className="flex items-center gap-2">
            <button
              type="button"
              className="relative rounded-full p-2 text-muted-foreground hover:bg-white/5 hover:text-white transition-colors"
            >
              <span className="sr-only">View notifications</span>
              <Bell className="h-5 w-5" />
              <span className="absolute right-2 top-2 h-2 w-2 rounded-full bg-primary ring-2 ring-background" />
            </button>
            <Link
              to="/ai-assistant"
              className="rounded-full bg-primary/10 p-2 text-primary hover:bg-primary/20 transition-colors"
            >
              <Sparkles className="h-5 w-5" />
            </Link>
          </div>

          <div className="hidden lg:block lg:h-6 lg:w-px lg:bg-white/10" aria-hidden="true" />

          <button className="flex items-center gap-x-3 p-1 rounded-full hover:bg-white/5 transition-colors pr-3">
            <div className="h-8 w-8 flex items-center justify-center rounded-full bg-primary/20 text-primary font-semibold ring-2 ring-white/10">
              {user?.fullName?.charAt(0).toUpperCase() || "U"}
            </div>
            <span className="hidden lg:flex lg:items-center">
              <span className="text-sm font-semibold leading-6 text-white" aria-hidden="true">
                {user?.fullName || "User"}
              </span>
              <ChevronDown className="ml-2 h-4 w-4 text-muted-foreground" aria-hidden="true" />
            </span>
          </button>
        </div>
      </div>
    </header>
  );
}
