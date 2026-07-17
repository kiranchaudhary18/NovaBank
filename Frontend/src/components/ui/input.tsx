import * as React from "react";
import { cn } from "@/lib/utils";

export interface InputProps extends React.InputHTMLAttributes<HTMLInputElement> {
  icon?: React.ReactNode;
  suffix?: React.ReactNode;
  error?: string;
  label?: string; // If provided, renders a floating label version
}

const Input = React.forwardRef<HTMLInputElement, InputProps>(
  ({ className, type, icon, suffix, error, label, id, ...props }, ref) => {
    const defaultId = React.useId();
    const inputId = id ?? defaultId;

    if (label) {
      return (
        <div className="w-full">
          <div
            className={cn(
              "group relative rounded-xl border bg-white/[0.02] transition duration-300",
              error
                ? "border-destructive/60 focus-within:border-destructive"
                : "border-border focus-within:border-primary/50 focus-within:ring-2 focus-within:ring-primary/20",
              className,
            )}
          >
            {icon && (
              <span className="pointer-events-none absolute left-3 top-1/2 -translate-y-1/2 text-muted-foreground transition-colors group-focus-within:text-primary">
                {icon}
              </span>
            )}
            <input
              id={inputId}
              type={type}
              placeholder=" "
              className={cn(
                "peer h-12 w-full bg-transparent px-3 pt-3.5 pb-1 text-sm outline-none transition-all placeholder-shown:pt-2.5",
                icon ? "pl-10" : "",
                suffix ? "pr-10" : "",
              )}
              ref={ref}
              {...props}
            />
            <label
              htmlFor={inputId}
              className={cn(
                "pointer-events-none absolute top-1 text-[11px] font-medium uppercase tracking-wider text-muted-foreground transition-all duration-300",
                "peer-placeholder-shown:top-1/2 peer-placeholder-shown:-translate-y-1/2 peer-placeholder-shown:text-sm peer-placeholder-shown:normal-case peer-placeholder-shown:tracking-normal",
                "peer-focus:top-1 peer-focus:translate-y-0 peer-focus:text-[11px] peer-focus:uppercase peer-focus:tracking-wider peer-focus:text-primary",
                icon ? "left-10" : "left-3",
              )}
            >
              {label}
            </label>
            {suffix && (
              <span className="absolute right-3 top-1/2 -translate-y-1/2 text-muted-foreground">
                {suffix}
              </span>
            )}
          </div>
          {error && <p className="mt-1.5 pl-1 text-xs font-medium text-destructive">{error}</p>}
        </div>
      );
    }

    return (
      <div className="relative w-full">
        {icon && (
          <span className="pointer-events-none absolute left-3 top-1/2 -translate-y-1/2 text-muted-foreground transition-colors focus-within:text-primary">
            {icon}
          </span>
        )}
        <input
          type={type}
          className={cn(
            "flex h-11 w-full rounded-xl border border-input bg-white/[0.02] px-3 py-1 text-sm shadow-sm transition-all duration-300 file:border-0 file:bg-transparent file:text-sm file:font-medium placeholder:text-muted-foreground",
            "focus-visible:border-primary/50 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-primary/20",
            "disabled:cursor-not-allowed disabled:opacity-50",
            icon && "pl-10",
            suffix && "pr-10",
            error &&
              "border-destructive/60 focus-visible:border-destructive focus-visible:ring-destructive/20",
            className,
          )}
          ref={ref}
          {...props}
        />
        {suffix && (
          <span className="absolute right-3 top-1/2 -translate-y-1/2 text-muted-foreground">
            {suffix}
          </span>
        )}
      </div>
    );
  },
);
Input.displayName = "Input";

export { Input };
