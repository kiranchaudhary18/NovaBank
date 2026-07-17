import { Link } from "@tanstack/react-router";

export function NovaLogo({ className = "" }: { className?: string }) {
  return (
    <Link to="/" className={`group inline-flex items-center gap-2.5 ${className}`}>
      <span className="relative grid h-9 w-9 place-items-center rounded-xl bg-gradient-primary shadow-glow">
        <svg viewBox="0 0 24 24" className="h-4.5 w-4.5" fill="none" aria-hidden>
          <path
            d="M4 18V6l8 12V6M20 6v12"
            stroke="white"
            strokeWidth="2.2"
            strokeLinecap="round"
            strokeLinejoin="round"
          />
        </svg>
        <span className="absolute inset-0 rounded-xl opacity-0 blur-xl transition-opacity duration-500 group-hover:opacity-70 bg-gradient-primary" />
      </span>
      <span className="font-display text-lg font-bold tracking-tight">
        Nova<span className="text-gradient">Bank</span>
      </span>
    </Link>
  );
}
