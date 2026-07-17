import { NovaLogo } from "@/components/brand/logo";
import { Github, Twitter, Linkedin, ArrowRight } from "lucide-react";
import { Link } from "@tanstack/react-router";

const cols = [
  {
    title: "Product",
    links: ["Accounts", "Cards", "Transfers", "Capital", "Analytics", "API"],
  },
  {
    title: "Company",
    links: ["About", "Careers", "Press", "Security", "Contact"],
  },
  {
    title: "Resources",
    links: ["Docs", "Guides", "Changelog", "Status", "System"],
  },
  {
    title: "Legal",
    links: ["Terms", "Privacy", "Cookies", "Licenses", "Disclosures"],
  },
];

export function Footer() {
  return (
    <footer className="relative border-t border-white/5 bg-background pt-20 pb-10">
      <div className="mx-auto max-w-[1400px] px-6">
        <div className="grid gap-16 lg:grid-cols-12 xl:gap-24 mb-20">
          <div className="lg:col-span-5">
            <NovaLogo />
            <p className="mt-6 max-w-sm text-base leading-relaxed text-muted-foreground">
              NovaBank is an AI-powered banking OS for modern global businesses. Licensed in the EU,
              UK, US and Singapore.
            </p>

            <div className="mt-8 flex items-center gap-4">
              <Link
                to="/auth/signup"
                className="text-sm font-bold text-white hover:text-primary transition-colors flex items-center gap-1 group"
              >
                Open Account{" "}
                <ArrowRight className="h-4 w-4 transition-transform group-hover:translate-x-1" />
              </Link>
            </div>
          </div>

          <div className="grid grid-cols-2 gap-10 sm:grid-cols-4 lg:col-span-7">
            {cols.map((c) => (
              <div key={c.title}>
                <div className="mb-6 font-display text-[13px] font-bold uppercase tracking-[0.15em] text-white">
                  {c.title}
                </div>
                <ul className="space-y-4">
                  {c.links.map((l) => (
                    <li key={l}>
                      <Link
                        to="/"
                        className="text-[15px] font-medium text-muted-foreground transition-colors hover:text-white"
                      >
                        {l}
                      </Link>
                    </li>
                  ))}
                </ul>
              </div>
            ))}
          </div>
        </div>

        <div className="flex flex-col items-start justify-between gap-6 border-t border-white/10 pt-10 sm:flex-row sm:items-center">
          <div className="flex items-center gap-3">
            {[Twitter, Github, Linkedin].map((I, i) => (
              <a
                key={i}
                href="/"
                className="grid h-10 w-10 place-items-center rounded-full bg-white/[0.03] text-muted-foreground transition hover:bg-white/[0.08] hover:text-white"
              >
                <I className="h-4 w-4" />
              </a>
            ))}
          </div>

          <div className="text-right">
            <p className="text-sm font-semibold text-white/80">
              © {new Date().getFullYear()} NovaBank Financial Technologies, Inc.
            </p>
            <p className="text-xs text-muted-foreground mt-1">
              Built for teams who take money seriously.
            </p>
          </div>
        </div>
      </div>
    </footer>
  );
}
