import { Search, Bell, Sparkles, ChevronDown, User, LogOut, Settings } from "lucide-react";
import { Link, useLocation, useNavigate } from "@tanstack/react-router";
import { useState } from "react";
import { AuthService } from "@/lib/api/services/auth.service";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { useCurrentUser } from "@/lib/api/hooks/use-auth";

export function TopNav() {
  const location = useLocation();
  const navigate = useNavigate();
  const currentPath = location.pathname;
  const { data: user } = useCurrentUser();
  const [searchQuery, setSearchQuery] = useState("");

  const handleLogout = () => {
    AuthService.logout();
    navigate({ to: "/auth/login" });
  };

  const handleSearch = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === "Enter" && searchQuery.trim()) {
      navigate({ to: "/transactions" });
      setSearchQuery("");
    }
  };

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
          <div className="relative">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
            <input
              type="text"
              placeholder="Search..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              onKeyDown={handleSearch}
              className="w-48 sm:w-64 rounded-xl border border-white/10 bg-white/5 pl-9 pr-12 py-1.5 text-sm text-white placeholder:text-muted-foreground outline-none focus:border-primary/50 focus:bg-white/10 transition-colors"
            />
            <kbd className="absolute right-2 top-1/2 -translate-y-1/2 hidden sm:inline-block rounded border border-white/20 bg-white/10 px-1.5 font-mono text-[10px] font-medium text-white/70">
              <span className="text-xs">⌘</span>K
            </kbd>
          </div>

          <div className="flex items-center gap-2">
            <Link
              to="/notifications"
              className="relative rounded-full p-2 text-muted-foreground hover:bg-white/5 hover:text-white transition-colors"
            >
              <span className="sr-only">View notifications</span>
              <Bell className="h-5 w-5" />
              <span className="absolute right-2 top-2 h-2 w-2 rounded-full bg-primary ring-2 ring-background" />
            </Link>
            <Link
              to="/ai-assistant"
              className="rounded-full bg-primary/10 p-2 text-primary hover:bg-primary/20 transition-colors"
            >
              <Sparkles className="h-5 w-5" />
            </Link>
          </div>

          <div className="hidden lg:block lg:h-6 lg:w-px lg:bg-white/10" aria-hidden="true" />

          <DropdownMenu>
            <DropdownMenuTrigger asChild>
              <button className="flex items-center gap-x-3 p-1 rounded-full hover:bg-white/5 transition-colors pr-3 outline-none">
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
            </DropdownMenuTrigger>
            <DropdownMenuContent align="end" className="w-56 glass border-white/10 text-white">
              <DropdownMenuLabel>My Account</DropdownMenuLabel>
              <DropdownMenuSeparator className="bg-white/10" />
              <DropdownMenuItem asChild className="hover:bg-white/10 cursor-pointer focus:bg-white/10 focus:text-white">
                <Link to="/settings" className="flex w-full items-center">
                  <User className="mr-2 h-4 w-4" />
                  <span>Profile</span>
                </Link>
              </DropdownMenuItem>
              <DropdownMenuItem asChild className="hover:bg-white/10 cursor-pointer focus:bg-white/10 focus:text-white">
                <Link to="/settings" className="flex w-full items-center">
                  <Settings className="mr-2 h-4 w-4" />
                  <span>Settings</span>
                </Link>
              </DropdownMenuItem>
              <DropdownMenuSeparator className="bg-white/10" />
              <DropdownMenuItem onClick={handleLogout} className="text-red-400 hover:bg-red-500/10 cursor-pointer focus:bg-red-500/10 focus:text-red-400">
                <LogOut className="mr-2 h-4 w-4" />
                <span>Log out</span>
              </DropdownMenuItem>
            </DropdownMenuContent>
          </DropdownMenu>
        </div>
      </div>
    </header>
  );
}
