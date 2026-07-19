import { createFileRoute } from "@tanstack/react-router";
import { toast } from "sonner";
import { motion, useMotionValue, useTransform, useSpring } from "framer-motion";
import { useQuery } from "@tanstack/react-query";
import { AccountService } from "@/lib/api/services/account.service";
import { CardService } from "@/lib/api/services/card.service";
import { CreditCard, Snowflake, Lock, Settings, EyeOff } from "lucide-react";

export const Route = createFileRoute("/_authenticated/cards")({
  component: CardsPage,
});

function CardsPage() {
  const { data: accounts = [] } = useQuery({
    queryKey: ["accounts"],
    queryFn: () => AccountService.getMyAccounts(),
  });

  const primaryAccount = accounts[0]?.accountNumber;

  const { data: cards = [], isLoading } = useQuery({
    queryKey: ["cards", primaryAccount],
    queryFn: () => CardService.getCards(primaryAccount as string),
    enabled: !!primaryAccount,
  });

  return (
    <div className="p-6 lg:p-8 max-w-7xl mx-auto space-y-8">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 mb-12">
        <motion.div
          initial={{ opacity: 0, y: 10 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.4 }}
        >
          <h2 className="text-2xl sm:text-3xl font-display font-bold text-white tracking-tight">
            Corporate Cards
          </h2>
          <p className="text-sm text-muted-foreground mt-1">
            Manage your physical and virtual corporate debit cards.
          </p>
        </motion.div>

        <motion.div
          initial={{ opacity: 0, scale: 0.95 }}
          animate={{ opacity: 1, scale: 1 }}
          transition={{ duration: 0.4, delay: 0.1 }}
        >
          <button onClick={() => toast.info('Feature Coming Soon', { description: 'This module is currently in development.' })}  className="rounded-xl bg-gradient-primary px-4 py-2 text-sm font-semibold text-white shadow-glow hover:opacity-95 transition-opacity">
            + Issue New Card
          </button>
        </motion.div>
      </div>

      {isLoading ? (
        <div className="flex items-center justify-center min-h-[300px]">
          <div className="h-8 w-8 animate-spin rounded-full border-4 border-primary border-t-transparent" />
        </div>
      ) : (
        <div className="grid lg:grid-cols-2 gap-12">
          {cards.map((card: any, idx: number) => (
            <CardComponent key={card.id} card={card} delay={idx * 0.2} />
          ))}
        </div>
      )}
    </div>
  );
}

function CardComponent({ card, delay }: { card: any; delay: number }) {
  const x = useMotionValue(0);
  const y = useMotionValue(0);

  const mouseXSpring = useSpring(x);
  const mouseYSpring = useSpring(y);

  const rotateX = useTransform(mouseYSpring, [-0.5, 0.5], ["17.5deg", "-17.5deg"]);
  const rotateY = useTransform(mouseXSpring, [-0.5, 0.5], ["-17.5deg", "17.5deg"]);

  const handleMouseMove = (e: React.MouseEvent<HTMLDivElement, MouseEvent>) => {
    const rect = e.currentTarget.getBoundingClientRect();
    const width = rect.width;
    const height = rect.height;
    const mouseX = e.clientX - rect.left;
    const mouseY = e.clientY - rect.top;
    const xPct = mouseX / width - 0.5;
    const yPct = mouseY / height - 0.5;
    x.set(xPct);
    y.set(yPct);
  };

  const handleMouseLeave = () => {
    x.set(0);
    y.set(0);
  };

  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.5, delay }}
      className="space-y-8"
    >
      <div
        style={{ perspective: 1000 }}
        className="relative group cursor-pointer max-w-sm mx-auto w-full"
        onMouseMove={handleMouseMove}
        onMouseLeave={handleMouseLeave}
      >
        <motion.div
          style={{ rotateX, rotateY, transformStyle: "preserve-3d" }}
          className={`relative aspect-[1.586/1] w-full rounded-2xl ${card.color} p-6 shadow-2xl overflow-hidden`}
        >
          {/* Card Shine Effect */}
          <div className="absolute inset-0 bg-gradient-to-tr from-white/0 via-white/10 to-white/0 opacity-0 group-hover:opacity-100 transition-opacity duration-700 pointer-events-none mix-blend-overlay" />

          <div className="absolute top-6 right-6">
            <svg
              width="40"
              height="24"
              viewBox="0 0 40 24"
              fill="none"
              xmlns="http://www.w3.org/2000/svg"
            >
              <circle cx="12" cy="12" r="12" fill="white" fillOpacity="0.5" />
              <circle cx="28" cy="12" r="12" fill="white" fillOpacity="0.5" />
            </svg>
          </div>

          <div className="absolute bottom-16 left-6">
            <p className="font-mono text-xl text-white tracking-[0.2em] shadow-sm">
              •••• •••• •••• {card.last4}
            </p>
          </div>

          <div className="absolute bottom-6 left-6 flex items-center justify-between w-[calc(100%-3rem)]">
            <div>
              <p className="text-[10px] text-white/70 font-semibold uppercase tracking-widest mb-1">
                Cardholder
              </p>
              <p className="text-sm font-semibold text-white tracking-widest uppercase">
                {card.cardholder}
              </p>
            </div>
            <div className="text-right">
              <p className="text-[10px] text-white/70 font-semibold uppercase tracking-widest mb-1">
                Expires
              </p>
              <p className="text-sm font-semibold text-white tracking-widest">{card.expiry}</p>
            </div>
          </div>
        </motion.div>
      </div>

      <div className="rounded-3xl border glass p-6 max-w-sm mx-auto w-full space-y-6">
        <div>
          <div className="flex items-center justify-between mb-2">
            <h3 className="text-lg font-semibold text-white">{card.type} Card</h3>
            <span className="inline-flex items-center rounded-full bg-green-500/10 border border-green-500/20 px-2 py-0.5 text-xs font-medium text-green-400">
              {card.status}
            </span>
          </div>
          <div className="flex items-center justify-between text-sm text-muted-foreground">
            <span>Spent this month</span>
            <span>Limit: ${card.limit.toLocaleString()}</span>
          </div>
          <div className="mt-3">
            <div className="flex items-center justify-between mb-1">
              <span className="text-white font-semibold">${card.spent.toLocaleString()}</span>
              <span className="text-white/70 text-xs">
                {Math.round((card.spent / card.limit) * 100)}%
              </span>
            </div>
            <div className="h-2 w-full bg-white/5 rounded-full overflow-hidden">
              <div
                className="h-full bg-gradient-primary rounded-full"
                style={{ width: `${(card.spent / card.limit) * 100}%` }}
              />
            </div>
          </div>
        </div>

        <div className="grid grid-cols-4 gap-2 pt-4 border-t border-white/10">
          <button onClick={() => toast.info('Feature Coming Soon', { description: 'This module is currently in development.' })}  className="flex flex-col items-center gap-2 p-2 rounded-xl hover:bg-white/5 transition-colors group">
            <div className="w-10 h-10 rounded-full bg-white/5 border border-white/10 flex items-center justify-center group-hover:bg-primary/20 group-hover:border-primary/50 transition-colors">
              <EyeOff className="w-4 h-4 text-muted-foreground group-hover:text-primary" />
            </div>
            <span className="text-xs text-muted-foreground group-hover:text-white transition-colors">
              Details
            </span>
          </button>

          <button onClick={() => toast.info('Feature Coming Soon', { description: 'This module is currently in development.' })}  className="flex flex-col items-center gap-2 p-2 rounded-xl hover:bg-white/5 transition-colors group">
            <div className="w-10 h-10 rounded-full bg-white/5 border border-white/10 flex items-center justify-center group-hover:bg-white/10 transition-colors">
              <Lock className="w-4 h-4 text-muted-foreground group-hover:text-white" />
            </div>
            <span className="text-xs text-muted-foreground group-hover:text-white transition-colors">
              PIN
            </span>
          </button>

          <button onClick={() => toast.info('Feature Coming Soon', { description: 'This module is currently in development.' })}  className="flex flex-col items-center gap-2 p-2 rounded-xl hover:bg-white/5 transition-colors group">
            <div className="w-10 h-10 rounded-full bg-white/5 border border-white/10 flex items-center justify-center group-hover:bg-red-500/20 group-hover:border-red-500/50 transition-colors">
              <Snowflake className="w-4 h-4 text-muted-foreground group-hover:text-red-400" />
            </div>
            <span className="text-xs text-muted-foreground group-hover:text-red-400 transition-colors">
              Freeze
            </span>
          </button>

          <button onClick={() => toast.info('Feature Coming Soon', { description: 'This module is currently in development.' })}  className="flex flex-col items-center gap-2 p-2 rounded-xl hover:bg-white/5 transition-colors group">
            <div className="w-10 h-10 rounded-full bg-white/5 border border-white/10 flex items-center justify-center group-hover:bg-white/10 transition-colors">
              <Settings className="w-4 h-4 text-muted-foreground group-hover:text-white" />
            </div>
            <span className="text-xs text-muted-foreground group-hover:text-white transition-colors">
              Settings
            </span>
          </button>
        </div>
      </div>
    </motion.div>
  );
}
