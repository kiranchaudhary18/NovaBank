import { motion } from "framer-motion";
import { Fingerprint, Lock, ShieldCheck, KeyRound, ScanFace, Server } from "lucide-react";
import { SectionHeader } from "./features";

const pillars = [
  {
    icon: ShieldCheck,
    title: "SOC 2 Type II · ISO 27001",
    body: "Independently audited on a rolling 90-day cycle.",
  },
  {
    icon: Lock,
    title: "AES-256 at rest, TLS 1.3",
    body: "Envelope-encrypted with hardware-backed HSM keys.",
  },
  {
    icon: Fingerprint,
    title: "Biometric Login",
    body: "FIDO2 / WebAuthn with hardware device attestation.",
  },
  {
    icon: ScanFace,
    title: "Liveness AI KYC",
    body: "Sub-second identity verification with anti-spoof ML.",
  },
  {
    icon: Server,
    title: "Region-pinned Data",
    body: "Data residency across EU, US, IN, SG and MENA.",
  },
  {
    icon: KeyRound,
    title: "Fraud Detection",
    body: "Adaptive AI models score every transaction instantly.",
  },
];

export function Security() {
  return (
    <section id="security" className="relative py-28 sm:py-36 bg-surface/20">
      <div className="absolute inset-x-0 top-0 h-px bg-gradient-to-r from-transparent via-white/5 to-transparent" />
      <div className="mx-auto max-w-[1400px] px-6">
        <div className="grid gap-16 lg:grid-cols-12 xl:gap-24">
          <div className="flex flex-col justify-center lg:col-span-5">
            <SectionHeader
              align="left"
              eyebrow="Security & Compliance"
              title={
                <>
                  Bank-grade trust,
                  <br />
                  by <span className="text-gradient-cyan">default</span>.
                </>
              }
              subtitle="A security posture engineered from the metal up — audited, encrypted, and continuously monitored so your team never has to choose between velocity and safety."
            />
            <div className="mt-10 flex flex-wrap items-center gap-3">
              {["SOC 2 Type II", "ISO 27001", "PCI DSS Level 1", "GDPR", "PSD2"].map((b) => (
                <span
                  key={b}
                  className="rounded-full border border-white/10 bg-white/[0.03] px-4 py-2 text-xs font-bold uppercase tracking-widest text-white/70 shadow-sm backdrop-blur-md"
                >
                  {b}
                </span>
              ))}
            </div>
          </div>

          <div className="lg:col-span-7">
            <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
              {pillars.map((p, i) => (
                <motion.div
                  key={p.title}
                  initial={{ opacity: 0, y: 25 }}
                  whileInView={{ opacity: 1, y: 0 }}
                  viewport={{ once: true, margin: "-50px" }}
                  transition={{ duration: 0.5, delay: i * 0.1 }}
                  className="group rounded-[24px] border border-white/5 glass p-6 transition-all duration-500 hover:-translate-y-1 hover:border-cyan-400/30 hover:bg-white/[0.04] hover:shadow-[0_10px_30px_-15px_rgba(0,212,255,0.2)]"
                >
                  <div className="flex flex-col gap-4">
                    <span className="grid h-12 w-12 place-items-center rounded-2xl bg-cyan-400/10 text-cyan-400 transition-transform duration-500 group-hover:scale-110">
                      <p.icon className="h-6 w-6 drop-shadow-md" />
                    </span>
                    <div>
                      <h3 className="font-display text-lg font-bold text-white group-hover:text-cyan-50 transition-colors">
                        {p.title}
                      </h3>
                      <p className="mt-2 text-[15px] leading-relaxed text-muted-foreground group-hover:text-white/80 transition-colors">
                        {p.body}
                      </p>
                    </div>
                  </div>
                </motion.div>
              ))}
            </div>
          </div>
        </div>
      </div>
    </section>
  );
}
