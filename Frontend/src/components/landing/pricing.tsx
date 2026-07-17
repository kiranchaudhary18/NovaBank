import { motion } from "framer-motion";
import { CheckCircle2, Star } from "lucide-react";
import { Button } from "@/components/ui/button";
import { cn } from "@/lib/utils";
import { SectionHeader } from "./features";

const plans = [
  {
    name: "Starter",
    price: "$0",
    description: "Perfect for freelancers and solo founders.",
    features: ["1 Virtual Card", "Basic Analytics", "Standard Support", "SEPA Transfers"],
    highlight: false,
  },
  {
    name: "Premium",
    price: "$29",
    description: "For scaling startups and small teams.",
    features: [
      "5 Metal Cards",
      "AI Money Copilot",
      "Priority Support",
      "Global SWIFT",
      "2% Cashback",
    ],
    highlight: true,
  },
  {
    name: "Business",
    price: "$99",
    description: "Advanced controls for mid-market.",
    features: [
      "Unlimited Cards",
      "Expense Workflows",
      "Dedicated AM",
      "Custom Limits",
      "API Access",
    ],
    highlight: false,
  },
  {
    name: "Enterprise",
    price: "Custom",
    description: "For publicly traded & regulated orgs.",
    features: [
      "On-Premise Vault",
      "White-Label UI",
      "SLA 99.99%",
      "Bespoke Integrations",
      "Direct Routing",
    ],
    highlight: false,
  },
];

export function Pricing() {
  return (
    <section id="pricing" className="relative py-28 sm:py-36 bg-surface/20">
      <div className="absolute inset-x-0 top-0 h-px bg-gradient-to-r from-transparent via-white/5 to-transparent" />
      <div className="mx-auto max-w-[1400px] px-6">
        <SectionHeader
          eyebrow="Transparent Pricing"
          title={
            <>
              Scale without <span className="text-gradient">friction</span>.
            </>
          }
          subtitle="Choose the perfect plan for your business needs. No hidden fees."
        />
        <div className="mt-20 grid grid-cols-1 gap-6 md:grid-cols-2 lg:grid-cols-4 items-center">
          {plans.map((plan, i) => (
            <motion.div
              key={plan.name}
              initial={{ opacity: 0, y: 30 }}
              whileInView={{ opacity: 1, y: 0 }}
              viewport={{ once: true, margin: "-50px" }}
              transition={{ duration: 0.5, delay: i * 0.1 }}
              className={cn(
                "relative flex flex-col rounded-[32px] p-8 transition-all duration-500",
                plan.highlight
                  ? "glass-strong border border-primary/50 shadow-[0_0_80px_-20px_rgba(109,94,247,0.4)] scale-100 lg:scale-105 z-10"
                  : "glass border border-white/10 hover:border-white/20 hover:-translate-y-1",
              )}
            >
              {plan.highlight && (
                <div className="absolute -top-4 inset-x-0 flex justify-center">
                  <span className="flex items-center gap-1.5 rounded-full bg-gradient-primary px-3 py-1 text-[11px] font-bold uppercase tracking-wider text-white shadow-glow">
                    <Star className="h-3 w-3 fill-current" />
                    Most Popular
                  </span>
                </div>
              )}

              <div className="mb-4">
                <h3 className="text-xl font-display font-bold text-white">{plan.name}</h3>
                <p className="mt-2 text-sm text-muted-foreground h-10">{plan.description}</p>
              </div>
              <div className="mb-8 flex items-baseline gap-1 font-display">
                <span className="text-4xl font-extrabold text-white">{plan.price}</span>
                {plan.price !== "Custom" && (
                  <span className="text-muted-foreground font-semibold">/mo</span>
                )}
              </div>
              <div className="flex-1 space-y-4 mb-8">
                {plan.features.map((feature) => (
                  <div key={feature} className="flex items-center gap-3">
                    <CheckCircle2
                      className={cn("h-5 w-5", plan.highlight ? "text-primary" : "text-white/40")}
                    />
                    <span className="text-sm font-medium text-white/80">{feature}</span>
                  </div>
                ))}
              </div>
              <Button
                variant={plan.highlight ? "default" : "outline"}
                className={cn(
                  "w-full rounded-2xl h-12 text-sm font-bold transition-all",
                  plan.highlight
                    ? "shadow-glow hover:shadow-[0_0_20px_rgba(109,94,247,0.6)]"
                    : "border-white/10 hover:bg-white/5",
                )}
              >
                {plan.price === "Custom" ? "Contact Sales" : "Get Started"}
              </Button>
            </motion.div>
          ))}
        </div>
      </div>
    </section>
  );
}
