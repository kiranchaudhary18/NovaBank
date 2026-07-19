import { createFileRoute, Link, useNavigate } from "@tanstack/react-router";
import { motion, AnimatePresence } from "framer-motion";
import { useMemo, useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { toast } from "sonner";
import {
  Eye,
  EyeOff,
  Loader2,
  Mail,
  Lock,
  User,
  ArrowRight,
  Check,
  ShieldCheck,
} from "lucide-react";
import { InputOTP, InputOTPGroup, InputOTPSlot } from "@/components/ui/input-otp";
import { useSignup, useVerifyOtp } from "@/lib/api/hooks/use-auth";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";

const schema = z.object({
  fullName: z.string().trim().min(2, "Enter your full name").max(80),
  email: z.string().trim().email("Enter a valid email"),
  password: z
    .string()
    .min(8, "At least 8 characters")
    .regex(/[A-Z]/, "Include an uppercase letter")
    .regex(/[0-9]/, "Include a number"),
  terms: z.literal(true, { errorMap: () => ({ message: "You must accept the terms" }) }),
});
type FormData = z.infer<typeof schema>;

export const Route = createFileRoute("/auth/signup")({
  head: () => ({
    meta: [
      { title: "Open your account — NovaBank" },
      { name: "description", content: "Create a NovaBank workspace in minutes." },
    ],
  }),
  component: SignupPage,
});

type Step = "form" | "otp" | "done";

function SignupPage() {
  const [step, setStep] = useState<Step>("form");
  const [show, setShow] = useState(false);
  const [email, setEmail] = useState("");
  const [otp, setOtp] = useState("");
  const nav = useNavigate();
  const signup = useSignup();
  const verify = useVerifyOtp();

  const {
    register,
    handleSubmit,
    watch,
    formState: { errors },
  } = useForm<FormData>({ resolver: zodResolver(schema) });

  const pw = watch("password") ?? "";
  const strength = useMemo(() => scorePassword(pw), [pw]);

  const onSubmit = async (data: FormData) => {
    try {
      await signup.mutateAsync({
        fullName: data.fullName,
        email: data.email,
        password: data.password,
      });
      setEmail(data.email);
      setStep("done");
      setTimeout(() => nav({ to: "/dashboard" }), 1400);
      toast.success("Account created successfully!");
    } catch {
      toast.error("Signup failed — please try again");
    }
  };

  const onVerify = async () => {
    if (otp.length !== 6) return;
    try {
      await verify.mutateAsync({ email, code: otp });
      setStep("done");
      setTimeout(() => nav({ to: "/dashboard" }), 1400);
    } catch {
      toast.error("Invalid or expired code");
    }
  };

  return (
    <AnimatePresence mode="wait">
      {step === "form" && (
        <motion.div
          key="form"
          initial={{ opacity: 0, y: 12 }}
          animate={{ opacity: 1, y: 0 }}
          exit={{ opacity: 0, y: -12 }}
          transition={{ duration: 0.35 }}
        >
          <div className="mb-6">
            <div className="flex gap-2 mb-4">
              <div className="h-1.5 w-8 rounded-full bg-primary shadow-glow" />
              <div className="h-1.5 w-8 rounded-full bg-white/10" />
              <div className="h-1.5 w-8 rounded-full bg-white/10" />
            </div>
            <h1 className="font-display text-2xl font-bold tracking-tight">Open your account</h1>
            <p className="mt-1 text-sm text-muted-foreground">
              Get instant access to accounts, cards, transfers and AI insights.
            </p>
          </div>

          <form onSubmit={handleSubmit(onSubmit)} className="space-y-3">
            <Input
              icon={<User className="h-4 w-4" />}
              label="Full name"
              error={errors.fullName?.message}
              {...register("fullName")}
              autoComplete="name"
            />
            <Input
              icon={<Mail className="h-4 w-4" />}
              label="Work email"
              type="email"
              error={errors.email?.message}
              {...register("email")}
              autoComplete="email"
            />
            <Input
              icon={<Lock className="h-4 w-4" />}
              label="Password"
              type={show ? "text" : "password"}
              error={errors.password?.message}
              {...register("password")}
              autoComplete="new-password"
              suffix={
                <button
                  type="button"
                  onClick={() => setShow((v) => !v)}
                  className="text-muted-foreground transition hover:text-foreground"
                  aria-label="Toggle password"
                >
                  {show ? <EyeOff className="h-4 w-4" /> : <Eye className="h-4 w-4" />}
                </button>
              }
            />

            <PasswordStrength score={strength} />

            <div className="mt-2 rounded-2xl border border-white/10 bg-white/[0.02] p-4 transition-colors hover:bg-white/[0.04]">
              <label className="flex cursor-pointer items-start gap-3 text-xs text-muted-foreground group">
                <div className="relative flex h-5 w-5 shrink-0 items-center justify-center rounded-[6px] border border-white/15 bg-white/[0.02] transition-colors group-hover:border-primary/50 mt-0.5">
                  <input
                    type="checkbox"
                    {...register("terms")}
                    className="peer absolute inset-0 opacity-0 cursor-pointer"
                  />
                  <motion.div
                    initial={false}
                    animate={{ scale: watch("terms") ? 1 : 0, opacity: watch("terms") ? 1 : 0 }}
                    className="absolute inset-0 rounded-[5px] bg-gradient-primary grid place-items-center shadow-glow"
                  >
                    <Check className="h-3 w-3 text-white" strokeWidth={3} />
                  </motion.div>
                </div>
                <span className="leading-relaxed">
                  I agree to NovaBank's{" "}
                  <a className="font-medium text-foreground hover:text-primary transition" href="#">
                    Terms of Service
                  </a>{" "}
                  and{" "}
                  <a className="font-medium text-foreground hover:text-primary transition" href="#">
                    Privacy Policy
                  </a>
                  . I understand my data is protected by 256-bit encryption.
                </span>
              </label>
              {errors.terms && (
                <p className="mt-3 pl-8 text-xs text-destructive">
                  {errors.terms.message as string}
                </p>
              )}
            </div>

            <Button type="submit" isLoading={signup.isPending} className="w-full">
              Create account
              <ArrowRight className="h-4 w-4 transition-transform group-hover:translate-x-0.5" />
            </Button>
          </form>

          <p className="mt-6 text-center text-sm text-muted-foreground">
            Already have an account?{" "}
            <Link
              to="/auth/login"
              className="font-semibold text-foreground hover:text-primary-glow"
            >
              Sign in
            </Link>
          </p>
        </motion.div>
      )}

      {step === "otp" && (
        <motion.div
          key="otp"
          initial={{ opacity: 0, y: 12 }}
          animate={{ opacity: 1, y: 0 }}
          exit={{ opacity: 0, y: -12 }}
        >
          <div className="mb-8 text-center">
            <div className="flex justify-center gap-2 mb-8">
              <div className="h-1.5 w-8 rounded-full bg-white/20" />
              <div className="h-1.5 w-8 rounded-full bg-primary shadow-glow" />
              <div className="h-1.5 w-8 rounded-full bg-white/10" />
            </div>
            <div className="mx-auto grid h-16 w-16 place-items-center rounded-2xl bg-gradient-primary shadow-glow">
              <ShieldCheck className="h-8 w-8 text-white" />
            </div>
            <h1 className="mt-6 font-display text-2xl font-bold tracking-tight">
              Verify your email
            </h1>
            <p className="mt-2 text-sm text-muted-foreground">
              We sent a 6-digit code to <span className="text-foreground font-medium">{email}</span>
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
            onClick={onVerify}
            disabled={otp.length !== 6}
            isLoading={verify.isPending}
            className="mt-6 w-full"
          >
            Verify & continue
          </Button>

          <p className="mt-6 text-center text-xs text-muted-foreground">
            Didn't get it?{" "}
            <button
              onClick={() => toast.success("Code re-sent")}
              className="font-semibold text-primary-glow hover:underline"
            >
              Resend code
            </button>
          </p>
        </motion.div>
      )}

      {step === "done" && (
        <motion.div
          key="done"
          initial={{ opacity: 0, scale: 0.95 }}
          animate={{ opacity: 1, scale: 1 }}
          className="text-center"
        >
          <div className="mx-auto flex justify-center gap-2 mb-8">
            <div className="h-1.5 w-8 rounded-full bg-white/20" />
            <div className="h-1.5 w-8 rounded-full bg-white/20" />
            <div className="h-1.5 w-8 rounded-full bg-success shadow-[0_0_15px_rgba(34,197,94,0.5)]" />
          </div>
          <div className="mx-auto grid h-20 w-20 animate-pulse-glow place-items-center rounded-[24px] bg-success shadow-[0_0_40px_-10px_rgba(34,197,94,0.7)]">
            <Check className="h-10 w-10 text-white" strokeWidth={3} />
          </div>
          <h1 className="mt-8 font-display text-3xl font-bold tracking-tight">Account ready!</h1>
          <p className="mt-3 text-sm text-muted-foreground">
            Redirecting you to your NovaBank workspace…
          </p>
        </motion.div>
      )}
    </AnimatePresence>
  );
}

function scorePassword(pw: string) {
  let s = 0;
  if (pw.length >= 8) s++;
  if (pw.length >= 12) s++;
  if (/[A-Z]/.test(pw)) s++;
  if (/[0-9]/.test(pw)) s++;
  if (/[^A-Za-z0-9]/.test(pw)) s++;
  return Math.min(s, 4);
}

function PasswordStrength({ score }: { score: number }) {
  const labels = ["Too weak", "Weak", "Fair", "Strong", "Excellent"];
  const colors = ["bg-destructive", "bg-destructive/80", "bg-warning", "bg-primary", "bg-success"];
  return (
    <div>
      <div className="flex gap-1.5">
        {[0, 1, 2, 3].map((i) => (
          <div
            key={i}
            className={`h-1 flex-1 rounded-full transition-all ${
              i < score ? colors[score] : "bg-white/5"
            }`}
          />
        ))}
      </div>
      <p className="mt-1.5 text-[11px] text-muted-foreground">
        Password strength: <span className="text-foreground">{labels[score]}</span>
      </p>
    </div>
  );
}
