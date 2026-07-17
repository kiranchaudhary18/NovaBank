import { createFileRoute, Link } from "@tanstack/react-router";
import { motion, AnimatePresence } from "framer-motion";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { useState } from "react";
import { toast } from "sonner";
import { Mail, Loader2, ArrowRight, KeyRound, Check } from "lucide-react";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { useRequestReset } from "@/lib/api/hooks/use-auth";

const schema = z.object({ email: z.string().trim().email("Enter a valid email") });
type FormData = z.infer<typeof schema>;

export const Route = createFileRoute("/auth/forgot-password")({
  head: () => ({
    meta: [
      { title: "Reset password — NovaBank" },
      { name: "description", content: "Reset your NovaBank password securely." },
    ],
  }),
  component: ForgotPage,
});

function ForgotPage() {
  const [sent, setSent] = useState(false);
  const [email, setEmail] = useState("");
  const request = useRequestReset();
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<FormData>({ resolver: zodResolver(schema) });

  const onSubmit = async (data: FormData) => {
    try {
      await request.mutateAsync(data.email);
      setEmail(data.email);
      setSent(true);
      toast.success("Reset link sent");
    } catch {
      toast.error("Could not send reset email");
    }
  };

  return (
    <AnimatePresence mode="wait">
      {!sent ? (
        <motion.div
          key="form"
          initial={{ opacity: 0, y: 12 }}
          animate={{ opacity: 1, y: 0 }}
          exit={{ opacity: 0 }}
        >
          <div className="mb-8">
            <div className="mb-6 grid h-16 w-16 place-items-center rounded-2xl bg-gradient-primary shadow-glow">
              <KeyRound className="h-8 w-8 text-white" />
            </div>
            <h1 className="font-display text-3xl font-bold tracking-tight">Forgot password?</h1>
            <p className="mt-2 text-sm text-muted-foreground">
              Enter your email and we'll send you a 6-digit verification code.
            </p>
          </div>

          <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
            <Input
              icon={<Mail className="h-4 w-4" />}
              label="Email"
              type="email"
              error={errors.email?.message}
              {...register("email")}
            />
            <Button type="submit" isLoading={request.isPending} className="w-full">
              Send reset code
              <ArrowRight className="h-4 w-4 transition-transform group-hover:translate-x-0.5" />
            </Button>
          </form>

          <p className="mt-8 text-center text-sm text-muted-foreground">
            Remembered it?{" "}
            <Link
              to="/auth/login"
              className="font-semibold text-foreground hover:text-primary-glow"
            >
              Back to sign in
            </Link>
          </p>
        </motion.div>
      ) : (
        <motion.div
          key="sent"
          initial={{ opacity: 0, scale: 0.96 }}
          animate={{ opacity: 1, scale: 1 }}
          className="text-center"
        >
          <div className="mx-auto grid h-20 w-20 animate-pulse-glow place-items-center rounded-[24px] bg-success shadow-[0_0_40px_-10px_rgba(34,197,94,0.7)]">
            <Check className="h-10 w-10 text-white" strokeWidth={3} />
          </div>
          <h1 className="mt-8 font-display text-3xl font-bold tracking-tight">Check your inbox</h1>
          <p className="mt-3 text-sm text-muted-foreground">
            We sent a reset code to <span className="text-foreground font-medium">{email}</span>
          </p>
          <Link
            to="/auth/reset-password"
            search={{ email }}
            className="mt-8 inline-flex h-14 w-full items-center justify-center gap-2 rounded-[18px] bg-gradient-primary px-8 py-3.5 text-sm font-semibold text-white shadow-[0_0_40px_-15px_rgba(109,94,247,0.5)] transition-all hover:-translate-y-0.5 hover:shadow-[0_0_60px_-10px_rgba(109,94,247,0.7)]"
          >
            Enter verification code
            <ArrowRight className="h-4 w-4 transition-transform group-hover:translate-x-0.5" />
          </Link>
        </motion.div>
      )}
    </AnimatePresence>
  );
}
