import { motion } from "framer-motion";
import {
  Wallet,
  Send,
  ShieldCheck,
  BarChart3,
  CreditCard,
  Sparkles,
  Landmark,
  Globe2,
  Bell,
} from "lucide-react";

const features = [
  {
    icon: Sparkles,
    title: "AI Money Copilot",
    body: "Ask questions in plain English — forecast runway, flag anomalies, or draft a wire instantly using our proprietary LLM.",
    colSpan: "lg:col-span-2",
  },
  {
    icon: CreditCard,
    title: "Premium Virtual Cards",
    body: "Issue virtual and metal cards with granular spend controls, MCC rules and limits.",
    colSpan: "lg:col-span-1",
  },
  {
    icon: Send,
    title: "Smart Transfers",
    body: "Push money to 180+ countries with SWIFT, SEPA, ACH and local rails.",
    colSpan: "lg:col-span-1",
  },
  {
    icon: BarChart3,
    title: "Expense Analytics",
    body: "Live dashboards for cashflow, unit economics and treasury, refreshed every 60 seconds.",
    colSpan: "lg:col-span-1",
  },
  {
    icon: ShieldCheck,
    title: "Enterprise Security",
    body: "Adaptive ML models score every transaction in <40ms and block risk before it lands.",
    colSpan: "lg:col-span-1",
  },
  {
    icon: Landmark,
    title: "Capital & Loans",
    body: "Access working capital lines with dynamic pricing and no hidden fees.",
    colSpan: "lg:col-span-1",
  },
  {
    icon: Globe2,
    title: "Automated Investments",
    body: "Deploy idle cash into high-yield treasury bills automatically to maximize returns.",
    colSpan: "lg:col-span-2",
  },
];

export function Features() {
  return (
    <section id="features" className="relative py-28 sm:py-36">
      <div className="mx-auto max-w-7xl px-6">
        <SectionHeader
          eyebrow="Platform Features"
          title={
            <>
              Everything you need,
              <br />
              beautifully <span className="text-gradient">engineered</span>.
            </>
          }
          subtitle="Nine core modules, one calm workspace. Deploy in minutes, scale to billions."
        />

        <div className="mt-20 grid grid-cols-1 gap-6 md:grid-cols-2 lg:grid-cols-3">
          {features.map((f, i) => (
            <motion.div
              key={f.title}
              initial={{ opacity: 0, y: 30 }}
              whileInView={{ opacity: 1, y: 0 }}
              viewport={{ once: true, margin: "-100px" }}
              transition={{ duration: 0.6, delay: (i % 3) * 0.1 }}
              className={`group relative overflow-hidden rounded-[32px] border border-white/10 glass-strong p-8 shadow-card transition-all duration-700 hover:-translate-y-1 hover:border-primary/50 hover:shadow-[0_20px_40px_-20px_rgba(109,94,247,0.4)] ${f.colSpan}`}
            >
              <div
                className="pointer-events-none absolute -right-20 -top-20 h-56 w-56 rounded-full opacity-0 blur-[60px] transition-opacity duration-700 group-hover:opacity-40"
                style={{ background: "var(--gradient-primary)" }}
              />
              <div className="relative flex h-14 w-14 items-center justify-center rounded-2xl border border-white/10 bg-white/[0.03] shadow-inner mb-6 group-hover:scale-110 transition-transform duration-500">
                <f.icon className="h-6 w-6 text-primary drop-shadow-[0_0_8px_rgba(109,94,247,0.8)]" />
              </div>
              <h3 className="font-display text-2xl font-bold text-white group-hover:text-primary-foreground transition-colors duration-300">
                {f.title}
              </h3>
              <p className="mt-3 text-base leading-relaxed text-muted-foreground group-hover:text-white/80 transition-colors duration-300">
                {f.body}
              </p>
            </motion.div>
          ))}
        </div>
      </div>
    </section>
  );
}

export function SectionHeader({
  eyebrow,
  title,
  subtitle,
  align = "center",
}: {
  eyebrow: string;
  title: React.ReactNode;
  subtitle?: string;
  align?: "left" | "center";
}) {
  const cls = align === "center" ? "text-center mx-auto" : "";
  return (
    <div className={`max-w-3xl ${cls}`}>
      <div
        className={`inline-flex items-center gap-2 rounded-full border border-border bg-white/[0.03] px-3 py-1 text-[11px] font-medium uppercase tracking-[0.2em] text-muted-foreground`}
      >
        {eyebrow}
      </div>
      <h2 className="mt-5 font-display text-4xl font-bold leading-tight tracking-tight sm:text-5xl">
        {title}
      </h2>
      {subtitle && <p className="mt-4 text-base text-muted-foreground sm:text-lg">{subtitle}</p>}
    </div>
  );
}
