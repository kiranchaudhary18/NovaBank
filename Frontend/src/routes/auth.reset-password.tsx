import { createFileRoute, Link, useNavigate } from "@tanstack/react-router";
import { motion, AnimatePresence } from "framer-motion";
import { useState } from "react";
import { z } from "zod";
import { toast } from "sonner";
import { Lock, Loader2, Eye, EyeOff, Check, ShieldCheck } from "lucide-react";
import { InputOTP, InputOTPGroup, InputOTPSlot } from "@/components/ui/input-otp";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { useResetPassword } from "@/lib/api/hooks/use-auth";

const searchSchema = z.object({ email: z.string().email().optional() });

export const Route = createFileRoute("/auth/reset-password")({
  validateSearch: (s) => searchSchema.parse(s),
  head: () => ({
    meta: [{ title: "Set new password — NovaBank" }],
  }),
  component: ResetPage,
});

function ResetPage() {
  const { email = "you@example.com" } = Route.useSearch();
  const nav = useNavigate();
  const reset = useResetPassword();
  const [step, setStep] = useState<"otp" | "password" | "done">("otp");
  const [otp, setOtp] = useState("");
  const [pw, setPw] = useState("");
  const [pw2, setPw2] = useState("");
  const [show, setShow] = useState(false);

  const submit = async () => {
    if (pw.length < 8) return toast.error("Password must be at least 8 characters");
    if (pw !== pw2) return toast.error("Passwords don't match");
    try {
      await reset.mutateAsync({ email, code: otp, password: pw });
      setStep("done");
      setTimeout(() => nav({ to: "/auth/login" }), 1500);
    } catch {
      toast.error("Reset failed");
    }
  };

  return (
    <AnimatePresence mode="wait">
      {step === "otp" && (
        <motion.div
          key="otp"
          initial={{ opacity: 0, y: 12 }}
          animate={{ opacity: 1, y: 0 }}
          exit={{ opacity: 0 }}
        >
          <div className="mb-8 text-center">
            <div className="mx-auto grid h-16 w-16 place-items-center rounded-2xl bg-gradient-primary shadow-glow">
              <ShieldCheck className="h-8 w-8 text-white" />
            </div>
            <h1 className="mt-6 font-display text-2xl font-bold tracking-tight">
              Enter verification code
            </h1>
            <p className="mt-2 text-sm text-muted-foreground">
              Sent to <span className="text-foreground font-medium">{email}</span>
            </p>
          </div>
          <div className="flex justify-center">
            <InputOTP maxLength={6} value={otp} onChange={setOtp}>
              <InputOTPGroup className="gap-2">
                {Array.from({ length: 6 }).map((_, i) => (
                  <InputOTPSlot
                    key={i}
                    index={i}
                    className="h-14 w-12 rounded-xl border-border bg-white/[0.03] text-lg font-semibold shadow-soft"
                  />
                ))}
              </InputOTPGroup>
            </InputOTP>
          </div>
          <Button
            disabled={otp.length !== 6}
            onClick={() => setStep("password")}
            className="mt-6 w-full"
          >
            Continue
          </Button>
        </motion.div>
      )}

      {step === "password" && (
        <motion.div
          key="pw"
          initial={{ opacity: 0, y: 12 }}
          animate={{ opacity: 1, y: 0 }}
          exit={{ opacity: 0 }}
        >
          <div className="mb-8">
            <h1 className="font-display text-3xl font-bold">Set a new password</h1>
            <p className="mt-2 text-sm text-muted-foreground">
              Choose a strong password you haven't used before.
            </p>
          </div>
          <div className="space-y-4">
            <Input
              icon={<Lock className="h-4 w-4" />}
              label="New password"
              type={show ? "text" : "password"}
              value={pw}
              onChange={(e) => setPw(e.target.value)}
              suffix={
                <button
                  type="button"
                  onClick={() => setShow((v) => !v)}
                  className="text-muted-foreground hover:text-foreground"
                >
                  {show ? <EyeOff className="h-4 w-4" /> : <Eye className="h-4 w-4" />}
                </button>
              }
            />
            <Input
              icon={<Lock className="h-4 w-4" />}
              label="Confirm password"
              type={show ? "text" : "password"}
              value={pw2}
              onChange={(e) => setPw2(e.target.value)}
            />
            <Button onClick={submit} isLoading={reset.isPending} className="w-full">
              Update password
            </Button>
          </div>
        </motion.div>
      )}

      {step === "done" && (
        <motion.div
          key="done"
          initial={{ opacity: 0, scale: 0.96 }}
          animate={{ opacity: 1, scale: 1 }}
          className="text-center"
        >
          <div className="mx-auto grid h-20 w-20 animate-pulse-glow place-items-center rounded-[24px] bg-success shadow-[0_0_40px_-10px_rgba(34,197,94,0.7)]">
            <Check className="h-10 w-10 text-white" strokeWidth={3} />
          </div>
          <h1 className="mt-8 font-display text-3xl font-bold tracking-tight">Password updated</h1>
          <p className="mt-3 text-sm text-muted-foreground">Redirecting you to sign in…</p>
          <Link
            to="/auth/login"
            className="mt-8 inline-flex h-14 w-full items-center justify-center gap-2 rounded-[18px] bg-gradient-primary px-8 py-3.5 text-sm font-semibold text-white shadow-[0_0_40px_-15px_rgba(109,94,247,0.5)] transition-all hover:-translate-y-0.5 hover:shadow-[0_0_60px_-10px_rgba(109,94,247,0.7)]"
          >
            Continue to sign in
          </Link>
        </motion.div>
      )}
    </AnimatePresence>
  );
}
