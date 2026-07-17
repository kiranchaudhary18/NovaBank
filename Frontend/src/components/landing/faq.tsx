import { SectionHeader } from "./features";
import {
  Accordion,
  AccordionContent,
  AccordionItem,
  AccordionTrigger,
} from "@/components/ui/accordion";

const faqs = [
  {
    q: "How long does onboarding take?",
    a: "Most businesses are approved and funded in under 15 minutes. Enterprises typically go live within 3 business days after due diligence.",
  },
  {
    q: "Is NovaBank a bank?",
    a: "NovaBank operates as a licensed EMI in the EU/UK and partners with FDIC-insured banks in the US. Deposits are held in segregated accounts.",
  },
  {
    q: "What are the fees?",
    a: "Zero monthly minimums. Local transfers are free, international wires start at $8, and FX is charged at interbank + 0.35%.",
  },
  {
    q: "Do you support API access?",
    a: "Yes — every product is API-first. Full REST and webhooks documentation available at docs.novabank.io.",
  },
  {
    q: "Can I integrate my existing accounting stack?",
    a: "Native integrations with Xero, QuickBooks, NetSuite, Sage and 200+ other tools via our data platform.",
  },
];

export function FAQ() {
  return (
    <section id="faq" className="relative py-28 sm:py-36">
      <div className="mx-auto max-w-[900px] px-6">
        <SectionHeader
          eyebrow="Frequently Asked Questions"
          title={
            <>
              Everything you need
              <br />
              to <span className="text-gradient">know</span>.
            </>
          }
        />
        <Accordion type="single" collapsible className="mt-16 space-y-4">
          {faqs.map((f, i) => (
            <AccordionItem
              key={f.q}
              value={`i-${i}`}
              className="rounded-[24px] border border-white/10 glass px-6 py-2 transition-all duration-300 hover:border-white/20 data-[state=open]:glass-strong data-[state=open]:shadow-card"
            >
              <AccordionTrigger className="py-5 text-left font-display text-[17px] font-bold text-white hover:no-underline hover:text-primary transition-colors">
                {f.q}
              </AccordionTrigger>
              <AccordionContent className="pb-6 text-[15px] leading-relaxed text-muted-foreground">
                {f.a}
              </AccordionContent>
            </AccordionItem>
          ))}
        </Accordion>
      </div>
    </section>
  );
}
