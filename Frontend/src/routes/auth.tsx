import { createFileRoute, Outlet, Link } from "@tanstack/react-router";
import { motion, useMotionValue, useSpring, useTransform } from "framer-motion";
import { NovaLogo } from "@/components/brand/logo";
import { ArrowDownLeft, ShieldCheck, Wallet, Activity, CheckCircle2, Lock } from "lucide-react";
import React from "react";
import { cn } from "@/lib/utils";

export const Route = createFileRoute("/auth")({
  component: AuthLayout,
});

function AuthLayout() {
  return (
    <div className="relative grid h-[100dvh] grid-cols-1 lg:grid-cols-2 bg-background selection:bg-primary/40 text-foreground overflow-hidden">
      <BackgroundEffects />

      {/* Left — Immersive Showcase */}
      <div className="relative hidden lg:flex items-center justify-center p-6 lg:p-8 xl:p-12 overflow-hidden">
        <AuthShowcase />
      </div>

      {/* Right — Form Container */}
      <div className="relative flex flex-col z-10 lg:items-center lg:justify-center">
        <div className="flex items-center justify-between px-6 pt-6 lg:hidden z-20">
          <NovaLogo />
          <Link to="/" className="text-xs font-medium text-muted-foreground hover:text-foreground">
            ← Back to site
          </Link>
        </div>
        <div className="flex flex-1 items-center justify-center px-4 py-4 sm:px-8 z-10 w-full">
          <div className="w-full max-w-[480px] relative">
            <div className="absolute -inset-1 rounded-[32px] bg-gradient-to-b from-white/10 to-transparent opacity-50 blur-lg pointer-events-none" />
            <div className="relative glass-strong rounded-[24px] sm:rounded-[28px] border border-white/[0.08] p-6 sm:p-8 shadow-2xl backdrop-blur-3xl bg-surface/40">
              <Outlet />
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

function BackgroundEffects() {
  return (
    <div className="pointer-events-none absolute inset-0 z-0 overflow-hidden">
      <div className="noise absolute inset-0 opacity-[0.03] mix-blend-overlay" />
      <div className="absolute inset-0 bg-[radial-gradient(ellipse_100%_100%_at_50%_0%,rgba(109,94,247,0.12),transparent_70%)]" />

      {/* Subtle Grid */}
      <div className="absolute inset-0 bg-[linear-gradient(rgba(255,255,255,0.02)_1px,transparent_1px),linear-gradient(90deg,rgba(255,255,255,0.02)_1px,transparent_1px)] bg-[size:4rem_4rem] [mask-image:radial-gradient(ellipse_60%_60%_at_50%_50%,#000_10%,transparent_100%)] opacity-50" />

      <motion.div
        animate={{
          scale: [1, 1.2, 1],
          opacity: [0.15, 0.25, 0.15],
          x: [0, 40, 0],
          y: [0, -40, 0],
        }}
        transition={{ duration: 15, repeat: Infinity, ease: "easeInOut" }}
        className="absolute -left-32 top-1/4 h-[600px] w-[600px] rounded-full bg-primary/20 blur-[120px]"
      />
      <motion.div
        animate={{
          scale: [1, 1.3, 1],
          opacity: [0.1, 0.2, 0.1],
          x: [0, -50, 0],
          y: [0, 50, 0],
        }}
        transition={{ duration: 18, repeat: Infinity, ease: "easeInOut", delay: 2 }}
        className="absolute bottom-1/4 right-0 h-[500px] w-[500px] rounded-full bg-accent/15 blur-[150px]"
      />
    </div>
  );
}

function AuthShowcase() {
  const mouseX = useMotionValue(0);
  const mouseY = useMotionValue(0);

  const springConfig = { damping: 50, stiffness: 400 };
  const smoothX = useSpring(mouseX, springConfig);
  const smoothY = useSpring(mouseY, springConfig);

  const handleMouseMove = (e: React.MouseEvent) => {
    const { clientX, clientY } = e;
    const { innerWidth, innerHeight } = window;
    const x = clientX / innerWidth - 0.5;
    const y = clientY / innerHeight - 0.5;
    mouseX.set(x);
    mouseY.set(y);
  };

  return (
    <div
      onMouseMove={handleMouseMove}
      className="relative flex w-full max-w-[540px] flex-col bg-transparent justify-center h-full"
      style={{ perspective: 2000 }}
    >
      <div className="z-50 mb-6 xl:mb-10">
        <NovaLogo />
      </div>

      <div
        className="relative flex w-full h-[240px] lg:h-[280px] xl:h-[340px] items-center justify-center mb-6 xl:mb-10 scale-75 lg:scale-90 xl:scale-100 origin-center"
        style={{ transformStyle: "preserve-3d" }}
      >
        {/* Balance Widget (Top Center-ish) */}
        <motion.div
          animate={{ y: [0, -8, 0] }}
          transition={{ duration: 6, repeat: Infinity, ease: "easeInOut" }}
          style={{
            x: useTransform(smoothX, [-0.5, 0.5], [-20, 20]),
            y: useTransform(smoothY, [-0.5, 0.5], [-20, 20]),
            z: 80,
          }}
          className="absolute -top-4 -right-2 z-30"
        >
          <div className="glass-strong rounded-2xl p-5 shadow-glow border border-white/10 w-64 backdrop-blur-xl">
            <div className="flex items-center justify-between mb-3">
              <div className="flex items-center gap-2 text-sm text-muted-foreground font-medium">
                <Wallet className="h-4 w-4" />
                Total Balance
              </div>
              <span className="flex h-5 items-center rounded-full bg-success/20 px-2 text-[10px] font-bold text-success">
                +2.4%
              </span>
            </div>
            <div className="font-numeric text-3xl font-bold text-white tracking-tight">
              $142,500<span className="text-muted-foreground/50">.00</span>
            </div>
          </div>
        </motion.div>

        {/* Floating Debit Card (Center) */}
        <motion.div
          animate={{ y: [0, 10, 0] }}
          transition={{ duration: 8, repeat: Infinity, ease: "easeInOut", delay: 1 }}
          style={{
            rotateX: useTransform(smoothY, [-0.5, 0.5], [12, -12]),
            rotateY: useTransform(smoothX, [-0.5, 0.5], [-12, 12]),
            z: 50,
          }}
          className="absolute z-20"
        >
          <div className="relative h-56 w-96 rounded-2xl p-6 overflow-hidden border border-white/20 shadow-[0_20px_50px_rgba(0,0,0,0.5)] backdrop-blur-md bg-gradient-to-tr from-white/10 to-white/5">
            <div className="absolute -right-20 -top-20 h-64 w-64 rounded-full bg-gradient-primary opacity-50 blur-3xl pointer-events-none" />
            <div className="relative h-full flex flex-col justify-between">
              <div className="flex justify-between items-start">
                <div className="flex items-center gap-2">
                  <div className="h-8 w-12 rounded bg-white/20" />
                </div>
                <span className="font-display font-bold text-xl text-white tracking-widest">
                  NOVA
                </span>
              </div>
              <div className="space-y-4">
                <div className="font-numeric text-2xl tracking-[0.2em] text-white/90 shadow-sm">
                  •••• •••• •••• 4289
                </div>
                <div className="flex justify-between text-xs font-medium uppercase tracking-wider text-white/70">
                  <span>Priya Raman</span>
                  <span>12/28</span>
                </div>
              </div>
            </div>
          </div>
        </motion.div>

        {/* Notification Widget (Bottom Left) */}
        <motion.div
          animate={{ y: [0, -6, 0] }}
          transition={{ duration: 7, repeat: Infinity, ease: "easeInOut", delay: 2 }}
          style={{
            x: useTransform(smoothX, [-0.5, 0.5], [30, -30]),
            y: useTransform(smoothY, [-0.5, 0.5], [30, -30]),
            z: 100,
          }}
          className="absolute bottom-2 -left-4 xl:-left-8 z-40"
        >
          <div className="glass-strong rounded-2xl p-4 shadow-soft border border-white/10 w-72 flex items-center gap-4 backdrop-blur-xl">
            <div className="grid h-10 w-10 shrink-0 place-items-center rounded-full bg-success/10 text-success">
              <ArrowDownLeft className="h-5 w-5" />
            </div>
            <div>
              <p className="text-sm font-semibold text-white">Stripe Payout</p>
              <p className="text-xs text-muted-foreground">Today, 2:45 PM</p>
            </div>
            <div className="ml-auto font-numeric text-sm font-bold text-success">+$12,500</div>
          </div>
        </motion.div>

        {/* Activity Widget (Bottom Right overlapping card) */}
        <motion.div
          animate={{ y: [0, 8, 0] }}
          transition={{ duration: 5, repeat: Infinity, ease: "easeInOut", delay: 0.5 }}
          style={{
            x: useTransform(smoothX, [-0.5, 0.5], [20, -20]),
            y: useTransform(smoothY, [-0.5, 0.5], [40, -40]),
            z: 60,
          }}
          className="absolute -bottom-2 -right-4 xl:-right-6 z-10"
        >
          <div className="glass rounded-2xl p-5 shadow-soft border border-white/10 w-56 backdrop-blur-xl bg-white/[0.04]">
            <div className="flex items-center gap-2 text-sm text-muted-foreground font-medium mb-4">
              <Activity className="h-4 w-4" />
              Weekly Spend
            </div>
            <div className="flex items-end gap-2 h-16">
              {[40, 70, 45, 90, 60, 30, 80].map((h, i) => (
                <motion.div
                  key={i}
                  initial={{ height: 0 }}
                  animate={{ height: `${h}%` }}
                  transition={{ delay: 0.5 + i * 0.1, duration: 0.8, ease: "easeOut" }}
                  className={cn(
                    "w-full rounded-sm",
                    i === 3 ? "bg-primary shadow-[0_0_10px_rgba(109,94,247,0.5)]" : "bg-white/10",
                  )}
                />
              ))}
            </div>
          </div>
        </motion.div>
      </div>

      <motion.div
        initial={{ opacity: 0, y: 16 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.8, delay: 0.2 }}
        className="z-10 mt-2"
      >
        <h1 className="font-display text-3xl lg:text-4xl xl:text-5xl font-extrabold leading-[1.1] tracking-tight text-white">
          Banking,
          <br />
          Reimagined for the
          <br />
          <span className="text-gradient">Next Generation.</span>
        </h1>
        <p className="mt-3 text-sm lg:text-base text-muted-foreground max-w-md leading-relaxed">
          The ultimate financial operating system built for modern teams. Secure, fast, and
          remarkably beautiful.
        </p>

        <div className="mt-6 flex flex-wrap items-center gap-3 xl:gap-6">
          <div className="flex items-center gap-2 text-[11px] xl:text-xs font-medium text-muted-foreground/80">
            <ShieldCheck className="h-3.5 w-3.5 text-primary" />
            <span>256-bit AES Encryption</span>
          </div>
          <div className="flex items-center gap-2 text-[11px] xl:text-xs font-medium text-muted-foreground/80">
            <Lock className="h-3.5 w-3.5 text-primary" />
            <span>PCI DSS Certified</span>
          </div>
          <div className="flex items-center gap-2 text-[11px] xl:text-xs font-medium text-muted-foreground/80">
            <CheckCircle2 className="h-3.5 w-3.5 text-primary" />
            <span>SOC 2 Type II</span>
          </div>
        </div>
      </motion.div>
    </div>
  );
}
