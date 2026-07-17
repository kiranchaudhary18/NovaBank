import { motion } from "framer-motion";
import { Quote, BadgeCheck } from "lucide-react";
import { SectionHeader } from "./features";

const testimonials = [
  {
    q: "NovaBank replaced four vendors and shaved 11 days off our monthly close. The AI copilot alone paid for itself in a week.",
    a: "Priya Raman",
    r: "CFO, Meridian Robotics",
  },
  {
    q: "The card program is stunning — literally and operationally. Our team finally enjoys expensing without the usual headache.",
    a: "Diego Alvarez",
    r: "Head of Finance, Kite Labs",
  },
  {
    q: "We settle payouts in 42 countries. NovaBank is the only platform that made cross-border compliance feel entirely invisible.",
    a: "Yuki Tanaka",
    r: "COO, Northbeam",
  },
];

export function Testimonials() {
  return (
    <section className="relative py-28 sm:py-36 bg-background">
      <div className="absolute inset-x-0 top-0 h-px bg-gradient-to-r from-transparent via-white/5 to-transparent" />
      <div className="mx-auto max-w-[1400px] px-6">
        <SectionHeader
          eyebrow="Verified Testimonials"
          title={
            <>
              Trusted by the world's
              <br />
              most <span className="text-gradient">innovative teams</span>.
            </>
          }
        />
        <div className="mt-20 grid gap-6 md:grid-cols-3">
          {testimonials.map((t, i) => (
            <motion.figure
              key={t.a}
              initial={{ opacity: 0, y: 30 }}
              whileInView={{ opacity: 1, y: 0 }}
              viewport={{ once: true, margin: "-50px" }}
              transition={{ duration: 0.5, delay: i * 0.1 }}
              className="group flex h-full flex-col rounded-[32px] border border-white/10 glass-strong p-8 shadow-card transition-all duration-500 hover:-translate-y-1 hover:border-primary/30 hover:shadow-[0_20px_40px_-20px_rgba(109,94,247,0.3)]"
            >
              <div className="mb-6 flex h-12 w-12 items-center justify-center rounded-2xl bg-white/[0.04] border border-white/5 transition-transform duration-500 group-hover:scale-110">
                <Quote className="h-5 w-5 text-primary drop-shadow-md" />
              </div>
              <blockquote className="flex-1 font-display text-[17px] leading-relaxed text-foreground/90">
                "{t.q}"
              </blockquote>
              <figcaption className="mt-8 flex items-center gap-4 pt-6 relative before:absolute before:inset-x-0 before:top-0 before:h-px before:bg-gradient-to-r before:from-white/10 before:to-transparent">
                <div className="grid h-12 w-12 place-items-center rounded-full bg-gradient-primary shadow-glow font-display text-base font-bold text-white">
                  {t.a
                    .split(" ")
                    .map((n) => n[0])
                    .join("")}
                </div>
                <div>
                  <div className="flex items-center gap-1.5 text-base font-bold text-white">
                    {t.a}
                    <BadgeCheck className="h-4 w-4 text-accent drop-shadow-sm" />
                  </div>
                  <div className="text-[13px] text-muted-foreground mt-0.5">{t.r}</div>
                </div>
              </figcaption>
            </motion.figure>
          ))}
        </div>
      </div>
    </section>
  );
}
