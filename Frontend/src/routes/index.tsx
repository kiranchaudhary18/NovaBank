import { createFileRoute } from "@tanstack/react-router";
import { LandingNav } from "@/components/landing/nav";
import { Hero } from "@/components/landing/hero";
import { Stats } from "@/components/landing/stats";
import { Features } from "@/components/landing/features";
import { Security } from "@/components/landing/security";
import { AIFeatures } from "@/components/landing/ai-features";
import { Testimonials } from "@/components/landing/testimonials";
import { Pricing } from "@/components/landing/pricing";
import { FAQ } from "@/components/landing/faq";
import { Footer } from "@/components/landing/footer";

export const Route = createFileRoute("/")({
  component: Landing,
});

function Landing() {
  return (
    <main className="relative min-h-screen bg-background selection:bg-primary/30">
      <LandingNav />
      <Hero />
      <Stats />
      <Features />
      <AIFeatures />
      <Security />
      <Testimonials />
      <Pricing />
      <FAQ />
      <Footer />
    </main>
  );
}
