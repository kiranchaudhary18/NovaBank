import { createFileRoute, Link, useNavigate } from "@tanstack/react-router";
import { motion } from "framer-motion";
import { useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { Eye, EyeOff, Loader2, Mail, Lock, ArrowRight } from "lucide-react";
import { toast } from "sonner";
import { useLogin } from "@/lib/api/hooks/use-auth";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";

const schema = z.object({
  email: z.string().trim().email("Enter a valid email"),
  password: z.string().min(6, "At least 6 characters"),
  remember: z.boolean().optional(),
});
type FormData = z.infer<typeof schema>;

export const Route = createFileRoute("/auth/login")({
  head: () => ({
    meta: [
      { title: "Sign in — NovaBank" },
      { name: "description", content: "Access your NovaBank workspace." },
    ],
  }),
  component: LoginPage,
});

function LoginPage() {
  const [show, setShow] = useState(false);
  const nav = useNavigate();
  const login = useLogin();

  const {
    register,
    handleSubmit,
    watch,
    formState: { errors },
  } = useForm<FormData>({
    resolver: zodResolver(schema),
    defaultValues: { remember: true },
  });

  const onSubmit = async (data: FormData) => {
    try {
      await login.mutateAsync(data);
      toast.success("Welcome back to NovaBank");
      nav({ to: "/" });
    } catch {
      toast.error("Invalid credentials");
    }
  };

  return (
    <motion.div
      initial={{ opacity: 0, y: 12 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.5 }}
    >
      <div className="mb-6">
        <h1 className="font-display text-3xl font-bold tracking-tight">Welcome back</h1>
        <p className="mt-1 text-sm text-muted-foreground">Sign in to your NovaBank workspace.</p>
      </div>

      <SocialButtons />
      <Divider />

      <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
        <Input
          icon={<Mail className="h-4 w-4" />}
          label="Email"
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
          autoComplete="current-password"
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

        <div className="flex items-center justify-between pt-2 pb-4">
          <label className="flex cursor-pointer items-center gap-3 text-xs text-muted-foreground group">
            <div className="relative flex h-5 w-5 items-center justify-center rounded-[6px] border border-white/15 bg-white/[0.02] transition-colors group-hover:border-primary/50">
              <input
                type="checkbox"
                {...register("remember")}
                className="peer absolute inset-0 opacity-0 cursor-pointer"
              />
              <motion.div
                initial={false}
                animate={{ scale: watch("remember") ? 1 : 0, opacity: watch("remember") ? 1 : 0 }}
                className="absolute inset-0 rounded-[5px] bg-gradient-primary grid place-items-center shadow-glow"
              >
                <svg
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="white"
                  strokeWidth="3"
                  className="w-3 h-3"
                >
                  <polyline points="20 6 9 17 4 12" />
                </svg>
              </motion.div>
            </div>
            Remember me for 30 days
          </label>
          <Link
            to="/auth/forgot-password"
            className="text-xs font-semibold text-primary-glow hover:underline transition-all hover:text-primary"
          >
            Forgot password?
          </Link>
        </div>

        <Button type="submit" isLoading={login.isPending} className="w-full">
          Sign in
          <ArrowRight className="h-4 w-4 transition-transform group-hover:translate-x-0.5" />
        </Button>
      </form>

      <p className="mt-6 text-center text-sm text-muted-foreground">
        Don't have an account?{" "}
        <Link to="/auth/signup" className="font-semibold text-foreground hover:text-primary-glow">
          Open an account
        </Link>
      </p>
    </motion.div>
  );
}

function Divider() {
  return (
    <div className="my-6 flex items-center gap-3 text-[11px] uppercase tracking-widest text-muted-foreground">
      <span className="hairline flex-1" />
      or continue with email
      <span className="hairline flex-1" />
    </div>
  );
}

function SocialButtons() {
  return (
    <div className="grid grid-cols-2 gap-3">
      {[
        {
          label: "Google",
          svg: (
            <svg viewBox="0 0 24 24" className="h-4 w-4">
              <path
                fill="#EA4335"
                d="M12 10.2v3.9h5.5c-.24 1.4-1.66 4.1-5.5 4.1-3.3 0-6-2.7-6-6.1s2.7-6.1 6-6.1c1.9 0 3.1.8 3.8 1.5l2.6-2.5C16.8 3.5 14.7 2.5 12 2.5 6.8 2.5 2.6 6.7 2.6 12s4.2 9.5 9.4 9.5c5.4 0 9-3.8 9-9.2 0-.6-.1-1.1-.2-1.6H12z"
              />
            </svg>
          ),
        },
        {
          label: "Apple",
          svg: (
            <svg viewBox="0 0 24 24" className="h-4 w-4 fill-current">
              <path d="M16.5 12.5c0-2.5 2-3.7 2.1-3.8-1.1-1.6-2.9-1.8-3.5-1.9-1.5-.2-2.9.9-3.7.9-.8 0-1.9-.9-3.2-.8-1.6 0-3.1.9-4 2.4-1.7 3-.4 7.5 1.2 9.9.8 1.2 1.8 2.6 3.1 2.5 1.2 0 1.7-.8 3.2-.8s1.9.8 3.2.8c1.3 0 2.2-1.2 3-2.4.9-1.4 1.3-2.8 1.3-2.9-.1 0-2.5-1-2.5-3.9zM14.3 5c.6-.8 1.1-1.9 1-3-1 0-2.2.6-2.9 1.4-.6.7-1.2 1.9-1 3 1.1.1 2.3-.6 2.9-1.4z" />
            </svg>
          ),
        },
      ].map((b) => (
        <motion.button
          key={b.label}
          type="button"
          whileHover={{ scale: 1.02 }}
          whileTap={{ scale: 0.96 }}
          className="inline-flex items-center justify-center gap-2 rounded-[16px] border border-white/10 bg-white/[0.02] px-4 py-3.5 text-sm font-semibold text-foreground transition-all hover:bg-white/[0.06] hover:border-white/20 hover:shadow-[0_0_20px_-5px_rgba(255,255,255,0.1)]"
        >
          {b.svg}
          {b.label}
        </motion.button>
      ))}
    </div>
  );
}
