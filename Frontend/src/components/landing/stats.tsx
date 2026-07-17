import { motion, useInView } from "framer-motion";
import { useEffect, useRef, useState } from "react";

function useCountUp(target: number, duration = 1600) {
  const [val, setVal] = useState(0);
  const ref = useRef<HTMLSpanElement>(null);
  const inView = useInView(ref, { once: true, margin: "-80px" });
  useEffect(() => {
    if (!inView) return;
    const start = performance.now();
    let raf = 0;
    const tick = (now: number) => {
      const p = Math.min(1, (now - start) / duration);
      const eased = 1 - Math.pow(1 - p, 3);
      setVal(target * eased);
      if (p < 1) raf = requestAnimationFrame(tick);
    };
    raf = requestAnimationFrame(tick);
    return () => cancelAnimationFrame(raf);
  }, [inView, target, duration]);
  return { ref, val };
}

const stats = [
  { label: "Global Customers", value: 2.4, suffix: "M+", prefix: "", decimals: 1 },
  { label: "Daily Transactions", value: 450, suffix: "K+", prefix: "" },
  { label: "Countries Served", value: 65, suffix: "", prefix: "" },
  { label: "AI Predictions", value: 1.2, suffix: "B", prefix: "" },
];

export function Stats() {
  return (
    <section className="relative border-y border-border/60 bg-surface/40">
      <div className="mx-auto grid max-w-7xl grid-cols-2 gap-y-10 px-4 py-16 sm:px-6 md:grid-cols-4">
        {stats.map((s, i) => (
          <StatItem key={s.label} {...s} delay={i * 0.08} />
        ))}
      </div>
    </section>
  );
}

function StatItem({
  label,
  value,
  prefix = "",
  suffix = "",
  decimals = 0,
  delay,
}: {
  label: string;
  value: number;
  prefix?: string;
  suffix?: string;
  decimals?: number;
  delay: number;
}) {
  const { ref, val } = useCountUp(value);
  return (
    <motion.div
      initial={{ opacity: 0, y: 12 }}
      whileInView={{ opacity: 1, y: 0 }}
      viewport={{ once: true }}
      transition={{ duration: 0.5, delay }}
      className="text-center"
    >
      <div className="flex items-baseline justify-center font-display text-4xl font-bold tracking-tight sm:text-5xl">
        <span className="text-muted-foreground/50">{prefix}</span>
        <span ref={ref} data-numeric className="text-gradient">
          {val.toFixed(decimals)}
        </span>
        <span className="text-muted-foreground/70">{suffix}</span>
      </div>
      <div className="mt-2 text-xs uppercase tracking-[0.2em] text-muted-foreground">{label}</div>
    </motion.div>
  );
}
