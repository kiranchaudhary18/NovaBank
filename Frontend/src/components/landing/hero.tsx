import { motion, useMotionValue, useSpring, useTransform } from "framer-motion";
import { Link } from "@tanstack/react-router";
import {
  ArrowRight,
  Play,
  Sparkles,
  ArrowUpRight,
  ArrowDownLeft,
  TrendingUp,
  Wallet,
  Activity,
  CheckCircle2,
  ShieldCheck,
  CreditCard,
  Wifi,
} from "lucide-react";
import { Button } from "@/components/ui/button";
import React from "react";
import { cn } from "@/lib/utils";

export function Hero() {
  return (
    <section className="relative overflow-hidden pt-20 pb-28 sm:pt-32 sm:pb-36 lg:pt-40">
      {/* Background Ambient Effects */}
      <div className="pointer-events-none absolute inset-0 bg-hero-orbs" />
      <div className="pointer-events-none absolute inset-x-0 top-0 h-px bg-gradient-to-r from-transparent via-white/10 to-transparent" />
      <div className="noise pointer-events-none absolute inset-0 opacity-40 mix-blend-overlay" />

      {/* Grid pattern overlay */}
      <div className="absolute inset-0 bg-[linear-gradient(rgba(255,255,255,0.02)_1px,transparent_1px),linear-gradient(90deg,rgba(255,255,255,0.02)_1px,transparent_1px)] bg-[size:4rem_4rem] [mask-image:radial-gradient(ellipse_60%_50%_at_50%_0%,#000_10%,transparent_100%)] opacity-30 pointer-events-none" />

      <div className="relative mx-auto grid max-w-[1400px] grid-cols-1 gap-16 px-6 lg:grid-cols-2 lg:gap-12 xl:gap-20">
        {/* Left Content */}
        <div className="flex flex-col justify-center lg:py-12 z-10">
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.6 }}
            className="inline-flex items-center self-start gap-2 rounded-full border border-primary/30 bg-primary/10 px-3 py-1.5 mb-8"
          >
            <Sparkles className="h-4 w-4 text-primary" />
            <span className="text-xs font-semibold uppercase tracking-[0.15em] text-primary">
              NovaBank OS 3.0 Live
            </span>
          </motion.div>

          <motion.h1
            initial={{ opacity: 0, y: 24 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.65, delay: 0.1 }}
            className="font-display text-5xl font-extrabold leading-[1.05] tracking-tight sm:text-6xl lg:text-7xl"
          >
            Banking Built
            <br />
            For The <span className="text-gradient">AI Era</span>
          </motion.h1>

          <motion.p
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.6, delay: 0.2 }}
            className="mt-6 max-w-xl text-lg sm:text-xl text-muted-foreground leading-relaxed"
          >
            Modern digital banking platform powered by AI automation, instant payments, smart
            analytics and enterprise-grade security.
          </motion.p>

          <motion.div
            initial={{ opacity: 0, y: 16 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.5, delay: 0.3 }}
            className="mt-10 flex flex-wrap items-center gap-4"
          >
            <Button
              size="lg"
              asChild
              className="h-14 rounded-[18px] px-8 text-base shadow-glow group"
            >
              <Link to="/auth/signup">
                Open Account
                <ArrowRight className="ml-2 h-5 w-5 transition-transform group-hover:translate-x-1" />
              </Link>
            </Button>
            <Button
              size="lg"
              variant="outline"
              className="h-14 rounded-[18px] px-8 text-base bg-white/[0.02] hover:bg-white/[0.06] border-white/10 group"
            >
              <Play className="mr-2 h-4 w-4 text-primary transition-transform group-hover:scale-110" />
              Live Demo
            </Button>
          </motion.div>

          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            transition={{ delay: 0.6, duration: 1 }}
            className="mt-16 pt-8 border-t border-white/5"
          >
            <p className="text-xs font-semibold uppercase tracking-widest text-muted-foreground mb-4">
              Enterprise Grade Infrastructure
            </p>
            {/* Simple Marquee for Badges */}
            <div className="flex overflow-hidden relative w-full max-w-lg mask-horizontal">
              <div className="flex w-max min-w-full shrink-0 animate-marquee items-center gap-10 pr-10">
                {[
                  "Visa",
                  "Mastercard",
                  "UPI",
                  "PCI DSS",
                  "SOC2",
                  "ISO27001",
                  "Visa",
                  "Mastercard",
                  "UPI",
                  "PCI DSS",
                  "SOC2",
                  "ISO27001",
                ].map((badge, i) => (
                  <span
                    key={i}
                    className="flex items-center gap-2 font-display text-sm font-bold text-white/50 grayscale hover:grayscale-0 transition-all"
                  >
                    {badge === "PCI DSS" || badge === "SOC2" || badge === "ISO27001" ? (
                      <ShieldCheck className="w-4 h-4" />
                    ) : (
                      <CreditCard className="w-4 h-4" />
                    )}
                    {badge}
                  </span>
                ))}
              </div>
            </div>
          </motion.div>
        </div>

        {/* Right — 3D Immersive Showcase */}
        <div className="relative flex items-center justify-center lg:justify-end z-20 h-[500px] sm:h-[600px] w-full">
          <HeroShowcase />
        </div>
      </div>
    </section>
  );
}

function HeroShowcase() {
  const mouseX = useMotionValue(0);
  const mouseY = useMotionValue(0);

  const springConfig = { damping: 50, stiffness: 400 };
  const smoothX = useSpring(mouseX, springConfig);
  const smoothY = useSpring(mouseY, springConfig);

  const handleMouseMove = (e: React.MouseEvent) => {
    // We use currentTarget to restrict tracking to the showcase container bounds roughly
    const rect = e.currentTarget.getBoundingClientRect();
    const x = (e.clientX - rect.left) / rect.width - 0.5;
    const y = (e.clientY - rect.top) / rect.height - 0.5;
    mouseX.set(x * 2);
    mouseY.set(y * 2);
  };

  return (
    <div
      onMouseMove={handleMouseMove}
      className="relative w-full h-full max-w-[600px] flex items-center justify-center bg-transparent overflow-visible"
      style={{ perspective: 2000 }}
    >
      <div className="absolute inset-0 bg-primary/20 blur-[100px] rounded-full mix-blend-screen pointer-events-none" />

      {/* Center 3D Premium Card */}
      <motion.div
        animate={{ y: [0, 15, 0] }}
        transition={{ duration: 7, repeat: Infinity, ease: "easeInOut" }}
        style={{
          rotateX: useTransform(smoothY, [-1, 1], [15, -15]),
          rotateY: useTransform(smoothX, [-1, 1], [-20, 20]),
          z: 100,
        }}
        className="absolute z-30"
      >
        <div className="relative h-64 w-[420px] rounded-[24px] p-7 overflow-hidden border border-white/20 shadow-[0_30px_60px_-10px_rgba(0,0,0,0.6)] backdrop-blur-xl bg-gradient-to-tr from-white/10 to-white/5">
          <div className="absolute -right-10 -top-10 h-48 w-48 rounded-full bg-gradient-primary opacity-60 blur-3xl" />
          <div className="absolute -left-10 -bottom-10 h-32 w-32 rounded-full bg-accent opacity-40 blur-2xl" />

          <div className="relative h-full flex flex-col justify-between">
            <div className="flex justify-between items-start">
              <div className="flex items-center gap-2">
                <div className="h-9 w-14 rounded-md bg-white/20 shadow-inner" />
                <Wifi className="h-6 w-6 rotate-90 text-white/80 ml-2" />
              </div>
              <span className="font-display font-bold text-2xl text-white tracking-widest drop-shadow-md">
                NOVA
              </span>
            </div>

            <div className="space-y-4">
              <div className="font-numeric text-3xl tracking-[0.2em] text-white/95 shadow-sm drop-shadow-md">
                •••• •••• •••• 4289
              </div>
              <div className="flex justify-between text-sm font-semibold uppercase tracking-widest text-white/80">
                <span className="drop-shadow-sm">Priya Raman</span>
                <span className="drop-shadow-sm">12/28</span>
              </div>
            </div>
          </div>
        </div>
      </motion.div>

      {/* Balance Widget (Top Right) */}
      <motion.div
        animate={{ y: [0, -10, 0] }}
        transition={{ duration: 6, repeat: Infinity, ease: "easeInOut", delay: 1 }}
        style={{
          x: useTransform(smoothX, [-1, 1], [-40, 40]),
          y: useTransform(smoothY, [-1, 1], [-40, 40]),
          z: 150,
        }}
        className="absolute top-10 right-0 z-40 lg:-right-10"
      >
        <div className="glass-strong rounded-2xl p-5 shadow-glow border border-white/10 w-64 backdrop-blur-2xl bg-surface/80">
          <div className="flex items-center justify-between mb-3">
            <div className="flex items-center gap-2 text-sm text-muted-foreground font-medium">
              <Wallet className="h-4 w-4 text-primary" />
              Total Balance
            </div>
            <span className="flex h-5 items-center rounded-full bg-success/15 px-2 text-[10px] font-bold text-success border border-success/20">
              +4.2%
            </span>
          </div>
          <div className="font-numeric text-3xl font-bold text-white tracking-tight">
            $248,500<span className="text-muted-foreground/40 text-2xl">.00</span>
          </div>
        </div>
      </motion.div>

      {/* Notification Widget (Bottom Left) */}
      <motion.div
        animate={{ y: [0, 8, 0] }}
        transition={{ duration: 5.5, repeat: Infinity, ease: "easeInOut", delay: 0.5 }}
        style={{
          x: useTransform(smoothX, [-1, 1], [30, -30]),
          y: useTransform(smoothY, [-1, 1], [30, -30]),
          z: 80,
        }}
        className="absolute bottom-16 left-0 z-50 lg:-left-12"
      >
        <div className="glass-strong rounded-2xl p-4 shadow-soft border border-white/10 w-[300px] flex items-center gap-4 backdrop-blur-2xl bg-surface/70">
          <div className="grid h-10 w-10 shrink-0 place-items-center rounded-full bg-success/15 border border-success/20 text-success">
            <ArrowDownLeft className="h-5 w-5" />
          </div>
          <div>
            <p className="text-sm font-semibold text-white">Stripe Payout</p>
            <p className="text-xs text-muted-foreground">Today, 2:45 PM</p>
          </div>
          <div className="ml-auto font-numeric text-base font-bold text-success">+$24,500</div>
        </div>
      </motion.div>

      {/* Activity Chart Widget (Bottom Right, behind card) */}
      <motion.div
        animate={{ y: [0, 10, 0] }}
        transition={{ duration: 8, repeat: Infinity, ease: "easeInOut", delay: 2 }}
        style={{
          x: useTransform(smoothX, [-1, 1], [20, -20]),
          y: useTransform(smoothY, [-1, 1], [20, -20]),
          z: 40,
        }}
        className="absolute -bottom-4 right-10 z-10 lg:right-0"
      >
        <div className="glass rounded-2xl p-5 shadow-soft border border-white/10 w-[240px] backdrop-blur-xl bg-white/[0.03]">
          <div className="flex items-center gap-2 text-sm text-muted-foreground font-medium mb-4">
            <Activity className="h-4 w-4 text-accent" />
            Weekly Spend
          </div>
          <div className="flex items-end gap-1.5 h-16">
            {[40, 70, 45, 90, 60, 30, 80].map((h, i) => (
              <div
                key={i}
                className={cn(
                  "w-full rounded-sm transition-all duration-1000",
                  i === 3
                    ? "bg-gradient-primary shadow-[0_0_12px_rgba(109,94,247,0.6)]"
                    : "bg-white/10 hover:bg-white/20",
                )}
                style={{ height: `${h}%` }}
              />
            ))}
          </div>
        </div>
      </motion.div>

      {/* AI Insights (Top Left) */}
      <motion.div
        animate={{ y: [0, -12, 0] }}
        transition={{ duration: 9, repeat: Infinity, ease: "easeInOut", delay: 1.5 }}
        style={{
          x: useTransform(smoothX, [-1, 1], [25, -25]),
          y: useTransform(smoothY, [-1, 1], [25, -25]),
          z: 60,
        }}
        className="absolute top-20 -left-6 lg:-left-20 z-20"
      >
        <div className="glass rounded-full py-2.5 px-4 shadow-soft border border-white/10 flex items-center gap-3 backdrop-blur-md bg-white/[0.02]">
          <div className="grid h-6 w-6 shrink-0 place-items-center rounded-full bg-accent/20 text-accent">
            <Sparkles className="h-3 w-3" />
          </div>
          <p className="text-xs font-medium text-white/90">
            AI predicts <span className="text-accent font-bold">14% growth</span> this month
          </p>
        </div>
      </motion.div>
    </div>
  );
}
