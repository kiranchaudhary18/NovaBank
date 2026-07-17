import { motion } from "framer-motion";
import { SectionHeader } from "./features";
import { Sparkles, MessageSquare, TrendingUp, Search, Activity } from "lucide-react";
import { cn } from "@/lib/utils";

const conversations = [
  {
    q: "How much did we spend on cloud infrastructure last quarter?",
    a: "$182,430 across AWS, GCP and Cloudflare — 12% below forecast. Would you like the vendor breakdown?",
  },
  {
    q: "Draft a wire of $45,000 to Northwind Legal for retainer.",
    a: "Prepared. Beneficiary matched, memo attached, awaiting your approval.",
  },
  {
    q: "Forecast our runway assuming a 15% MoM burn increase.",
    a: "You'd hit 6 months of runway on Jan 18, 2027 — 47 days earlier than baseline.",
  },
];

export function AIFeatures() {
  return (
    <section id="ai" className="relative py-28 sm:py-36">
      <div className="mx-auto grid max-w-[1400px] grid-cols-1 gap-16 px-6 lg:grid-cols-12 xl:gap-24">
        <div className="flex flex-col justify-center lg:col-span-5 z-10">
          <SectionHeader
            align="left"
            eyebrow="Artificial Intelligence"
            title={
              <>
                Ask anything. <span className="text-gradient">Ship anything.</span>
              </>
            }
            subtitle="Nova AI is trained on your ledger, your policies and your workflows. It understands intent, respects your approvals, and never touches money without you."
          />
          <div className="mt-12 grid grid-cols-2 gap-4">
            {[
              { icon: MessageSquare, l: "Natural language" },
              { icon: TrendingUp, l: "Predictive insights" },
              { icon: Search, l: "Universal search" },
              { icon: Activity, l: "Automated analysis" },
            ].map((x) => (
              <div
                key={x.l}
                className="group rounded-[24px] border border-white/5 bg-white/[0.02] p-5 text-left transition-all hover:bg-white/[0.04]"
              >
                <div className="grid h-10 w-10 place-items-center rounded-xl bg-primary/10 text-primary transition-transform group-hover:scale-110">
                  <x.icon className="h-5 w-5 drop-shadow-md" />
                </div>
                <div className="mt-4 text-sm font-semibold text-white/90">{x.l}</div>
              </div>
            ))}
          </div>
        </div>

        <div className="lg:col-span-7 relative z-20">
          <div className="absolute -inset-10 rounded-[40px] bg-primary/20 blur-[80px] pointer-events-none" />
          <div className="relative overflow-hidden rounded-[32px] border border-white/10 glass-strong shadow-[0_30px_60px_-15px_rgba(0,0,0,0.8)]">
            <div className="flex items-center gap-3 border-b border-white/10 px-6 py-4 bg-white/[0.02]">
              <span className="grid h-8 w-8 place-items-center rounded-lg bg-gradient-primary shadow-glow">
                <Sparkles className="h-4 w-4 text-white" />
              </span>
              <span className="font-display font-semibold text-white">Nova AI Copilot</span>
              <span className="ml-auto flex items-center gap-1.5 rounded-full bg-success/15 px-3 py-1 text-[11px] font-bold text-success border border-success/20">
                <span className="relative flex h-2 w-2">
                  <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-success opacity-75"></span>
                  <span className="relative inline-flex rounded-full h-2 w-2 bg-success"></span>
                </span>
                Active
              </span>
            </div>

            <div className="space-y-6 p-6 sm:p-8 backdrop-blur-3xl bg-surface/40">
              {conversations.map((c, i) => (
                <motion.div
                  key={c.q}
                  initial={{ opacity: 0, y: 15 }}
                  whileInView={{ opacity: 1, y: 0 }}
                  viewport={{ once: true }}
                  transition={{ duration: 0.5, delay: i * 0.15 }}
                  className="space-y-3"
                >
                  <div className="ml-auto max-w-[85%] rounded-[24px] rounded-tr-sm bg-gradient-primary px-5 py-4 text-[15px] text-white shadow-glow">
                    {c.q}
                  </div>
                  <div className="max-w-[90%] rounded-[24px] rounded-tl-sm border border-white/10 bg-white/[0.03] px-5 py-4 text-[15px] leading-relaxed text-foreground/90 backdrop-blur-md shadow-sm">
                    {c.a}
                  </div>
                </motion.div>
              ))}

              <div className="mt-8 flex items-center gap-3 rounded-[20px] border border-white/10 glass p-2.5 pl-5 shadow-inner">
                <Sparkles className="h-5 w-5 text-primary" />
                <input
                  disabled
                  placeholder="Ask Nova to prepare a payment, forecast, or report…"
                  className="flex-1 bg-transparent text-[15px] placeholder:text-muted-foreground focus:outline-none"
                />
                <button className="rounded-xl bg-white px-5 py-3 text-sm font-bold text-background transition-transform hover:scale-105 active:scale-95 shadow-[0_0_20px_rgba(255,255,255,0.3)]">
                  Ask AI
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
}
