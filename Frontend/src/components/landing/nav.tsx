import { Link } from "@tanstack/react-router";
import { motion } from "framer-motion";
import { useState } from "react";
import { Menu, X } from "lucide-react";
import { NovaLogo } from "@/components/brand/logo";
import { Button } from "@/components/ui/button";

const links = [
  { label: "Features", href: "#features" },
  { label: "AI & Data", href: "#ai" },
  { label: "Security", href: "#security" },
  { label: "Pricing", href: "#pricing" },
  { label: "FAQ", href: "#faq" },
];

export function LandingNav() {
  const [open, setOpen] = useState(false);
  return (
    <motion.header
      initial={{ y: -20, opacity: 0 }}
      animate={{ y: 0, opacity: 1 }}
      transition={{ duration: 0.5, ease: "easeOut" }}
      className="fixed top-0 z-50 w-full"
    >
      <div className="mx-auto mt-4 max-w-7xl px-4 sm:px-6">
        <div className="glass-strong flex items-center justify-between rounded-2xl px-4 py-2.5 shadow-soft">
          <NovaLogo />
          <nav className="hidden items-center gap-1 md:flex">
            {links.map((l) => (
              <a
                key={l.href}
                href={l.href}
                className="rounded-lg px-3 py-2 text-sm text-muted-foreground transition-colors hover:bg-white/5 hover:text-foreground"
              >
                {l.label}
              </a>
            ))}
          </nav>
          <div className="hidden items-center gap-3 md:flex">
            <Button variant="ghost" asChild>
              <Link to="/auth/login">Sign in</Link>
            </Button>
            <Button asChild>
              <Link to="/auth/signup">Open account</Link>
            </Button>
          </div>
          <button
            onClick={() => setOpen((v) => !v)}
            className="grid h-9 w-9 place-items-center rounded-xl bg-white/5 md:hidden"
            aria-label="Toggle menu"
          >
            {open ? <X className="h-4 w-4" /> : <Menu className="h-4 w-4" />}
          </button>
        </div>
        {open && (
          <motion.div
            initial={{ opacity: 0, y: -6 }}
            animate={{ opacity: 1, y: 0 }}
            className="glass mt-2 rounded-2xl p-3 md:hidden"
          >
            <div className="flex flex-col">
              {links.map((l) => (
                <a
                  key={l.href}
                  href={l.href}
                  onClick={() => setOpen(false)}
                  className="rounded-lg px-3 py-2.5 text-sm text-muted-foreground hover:bg-white/5 hover:text-foreground"
                >
                  {l.label}
                </a>
              ))}
              <div className="mt-4 grid grid-cols-2 gap-2">
                <Button variant="outline" asChild>
                  <Link to="/auth/login">Sign in</Link>
                </Button>
                <Button asChild>
                  <Link to="/auth/signup">Open account</Link>
                </Button>
              </div>
            </div>
          </motion.div>
        )}
      </div>
    </motion.header>
  );
}
