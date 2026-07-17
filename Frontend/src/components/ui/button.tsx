/* eslint-disable react-refresh/only-export-components */
import * as React from "react";
import { Slot } from "@radix-ui/react-slot";
import { cva, type VariantProps } from "class-variance-authority";
import { motion } from "framer-motion";
import { Loader2 } from "lucide-react";

import { cn } from "@/lib/utils";

const buttonVariants = cva(
  "inline-flex items-center justify-center gap-2 whitespace-nowrap rounded-[18px] text-sm font-semibold transition-all duration-300 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:pointer-events-none disabled:opacity-60 [&_svg]:pointer-events-none [&_svg]:size-4 [&_svg]:shrink-0 cursor-pointer overflow-hidden",
  {
    variants: {
      variant: {
        default:
          "bg-gradient-primary text-white shadow-[0_0_40px_-15px_rgba(109,94,247,0.5)] hover:shadow-[0_0_60px_-10px_rgba(109,94,247,0.7)] hover:-translate-y-0.5",
        destructive: "bg-destructive text-destructive-foreground shadow-sm hover:bg-destructive/90",
        outline:
          "border border-white/10 bg-white/[0.02] shadow-sm hover:bg-white/[0.06] hover:text-white backdrop-blur-sm",
        secondary: "bg-secondary text-secondary-foreground shadow-sm hover:bg-secondary/80",
        ghost: "hover:bg-white/[0.06] hover:text-white",
        link: "text-primary underline-offset-4 hover:underline",
      },
      size: {
        default: "h-14 px-8 py-3.5",
        sm: "h-10 rounded-xl px-5 text-xs",
        lg: "h-16 rounded-[20px] px-10 text-base",
        icon: "h-14 w-14 rounded-2xl",
      },
    },
    defaultVariants: {
      variant: "default",
      size: "default",
    },
  },
);

export interface ButtonProps
  extends React.ButtonHTMLAttributes<HTMLButtonElement>, VariantProps<typeof buttonVariants> {
  asChild?: boolean;
  isLoading?: boolean;
}

const Button = React.forwardRef<HTMLButtonElement, ButtonProps>(
  ({ className, variant, size, asChild = false, isLoading, children, ...props }, ref) => {
    const Comp = asChild ? Slot : "button";

    const content = isLoading ? (
      <>
        <Loader2 className="mr-2 h-4 w-4 animate-spin" />
        {children}
      </>
    ) : (
      children
    );

    if (!asChild) {
      return (
        <motion.button
          whileHover={{ scale: 1.02 }}
          whileTap={{ scale: 0.95 }}
          className={cn(buttonVariants({ variant, size, className }))}
          // eslint-disable-next-line @typescript-eslint/no-explicit-any
          ref={ref as any}
          disabled={isLoading || props.disabled}
          // eslint-disable-next-line @typescript-eslint/no-explicit-any
          {...(props as any)}
        >
          {content}
        </motion.button>
      );
    }

    return (
      <Comp
        className={cn(buttonVariants({ variant, size, className }))}
        ref={ref}
        disabled={isLoading || props.disabled}
        {...props}
      >
        {content}
      </Comp>
    );
  },
);
Button.displayName = "Button";

export { Button, buttonVariants };
