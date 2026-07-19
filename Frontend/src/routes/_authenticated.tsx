import { Outlet, createFileRoute, redirect } from "@tanstack/react-router";
import { AppSidebar } from "@/components/dashboard/app-sidebar";
import { TopNav } from "@/components/dashboard/top-nav";

export const Route = createFileRoute("/_authenticated")({
  beforeLoad: async ({ context }) => {
    // Optionally check if user is authenticated here.
    // For now we will allow rendering mock views.
    return {
      user: { id: "mock-user", name: "Alex Sterling" },
    };
  },
  component: AuthenticatedLayout,
});

function AuthenticatedLayout() {
  return (
    <div className="flex h-screen w-full bg-background overflow-hidden text-foreground selection:bg-primary/30 relative">
      {/* Global Animated Background */}
      <div className="absolute inset-0 bg-hero-orbs opacity-40 mix-blend-screen pointer-events-none" />
      <div className="absolute inset-0 noise opacity-30 pointer-events-none mix-blend-overlay" />

      <AppSidebar />
      <div className="flex flex-1 flex-col overflow-hidden relative z-10">
        <TopNav />
        <main className="flex-1 overflow-y-auto scrollbar-none pb-20">
          <Outlet />
        </main>
      </div>
    </div>
  );
}
