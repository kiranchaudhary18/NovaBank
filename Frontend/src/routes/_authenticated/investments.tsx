import { createFileRoute } from "@tanstack/react-router";
import { toast } from "sonner";
import { motion } from "framer-motion";
import { TrendingUp, ArrowUpRight, ArrowDownRight, Activity, DollarSign } from "lucide-react";
import { useState } from "react";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";

export const Route = createFileRoute("/_authenticated/investments")({
  component: InvestmentsPage,
});



function InvestmentsPage() {
  const [investments, setInvestments] = useState<{ id: string; asset: string; amount: string; currentVal: string; trend: string }[]>([]);
  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const [asset, setAsset] = useState("");
  const [amount, setAmount] = useState("");

  const handleTrade = (e: React.FormEvent) => {
    e.preventDefault();
    if (!asset || !amount) return;
    const newInv = {
      id: Math.random().toString(36).substr(2, 9),
      asset,
      amount: "$" + parseFloat(amount).toLocaleString(),
      currentVal: "$" + (parseFloat(amount) * 1.05).toLocaleString(), // 5% mock gain
      trend: "+5.0%"
    };
    setInvestments([...investments, newInv]);
    setIsDialogOpen(false);
    toast.success("Trade executed successfully!");
    setAsset("");
    setAmount("");
  };

  return (
    <div className="p-6 lg:p-8 max-w-7xl mx-auto space-y-8">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 mb-8">
        <motion.div
          initial={{ opacity: 0, y: 10 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.4 }}
        >
          <h2 className="text-2xl sm:text-3xl font-display font-bold text-white tracking-tight">
            Treasury & Investments
          </h2>
          <p className="text-sm text-muted-foreground mt-1">
            Grow your idle cash with premium investment vehicles.
          </p>
        </motion.div>

        <motion.div
          initial={{ opacity: 0, scale: 0.95 }}
          animate={{ opacity: 1, scale: 1 }}
          transition={{ duration: 0.4, delay: 0.1 }}
        >
          <Dialog open={isDialogOpen} onOpenChange={setIsDialogOpen}>
            <DialogTrigger asChild>
              <button className="rounded-xl bg-gradient-primary px-4 py-2 text-sm font-semibold text-white shadow-glow hover:opacity-95 transition-opacity">
                Trade Assets
              </button>
            </DialogTrigger>
            <DialogContent className="glass border-white/10 text-white sm:max-w-md">
              <DialogHeader>
                <DialogTitle>Trade Asset</DialogTitle>
              </DialogHeader>
              <form onSubmit={handleTrade} className="space-y-4 pt-4">
                <div className="space-y-2">
                  <label className="text-sm font-medium text-white/70">Select Asset</label>
                  <select 
                    value={asset} 
                    onChange={(e) => setAsset(e.target.value)}
                    className="w-full rounded-lg bg-white/5 border border-white/10 px-3 py-2 text-white outline-none focus:border-primary/50 transition-colors"
                  >
                    <option value="" className="bg-[#060816]">Choose...</option>
                    <option value="AAPL - Apple Inc." className="bg-[#060816]">Apple (AAPL)</option>
                    <option value="TSLA - Tesla Inc." className="bg-[#060816]">Tesla (TSLA)</option>
                    <option value="BTC - Bitcoin" className="bg-[#060816]">Bitcoin (BTC)</option>
                    <option value="GOOGL - Alphabet Inc." className="bg-[#060816]">Google (GOOGL)</option>
                  </select>
                </div>
                <div className="space-y-2">
                  <label className="text-sm font-medium text-white/70">Amount (USD)</label>
                  <input 
                    type="number" 
                    value={amount} 
                    onChange={(e) => setAmount(e.target.value)}
                    className="w-full rounded-lg bg-white/5 border border-white/10 px-3 py-2 text-white outline-none focus:border-primary/50 transition-colors" 
                    placeholder="Enter amount" 
                    min="100"
                  />
                </div>
                <button type="submit" className="w-full rounded-lg bg-gradient-primary py-2.5 font-semibold text-white hover:opacity-95 transition-opacity">
                  Execute Trade
                </button>
              </form>
            </DialogContent>
          </Dialog>
        </motion.div>
      </div>

      {investments.length > 0 ? (
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          className="rounded-2xl border glass overflow-hidden"
        >
          <Table>
            <TableHeader>
              <TableRow className="border-b border-white/5 hover:bg-transparent">
                <TableHead className="text-muted-foreground font-medium">Asset</TableHead>
                <TableHead className="text-muted-foreground font-medium">Invested Amount</TableHead>
                <TableHead className="text-muted-foreground font-medium">Current Value</TableHead>
                <TableHead className="text-muted-foreground font-medium text-right">Trend</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {investments.map((inv) => (
                <TableRow key={inv.id} className="border-b border-white/5 hover:bg-white/5 transition-colors">
                  <TableCell className="font-medium text-white">{inv.asset}</TableCell>
                  <TableCell className="text-muted-foreground">{inv.amount}</TableCell>
                  <TableCell className="font-semibold text-white">{inv.currentVal}</TableCell>
                  <TableCell className="text-right text-green-400 font-semibold">{inv.trend}</TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </motion.div>
      ) : (
        <div className="flex flex-col items-center justify-center py-20 text-center glass rounded-3xl border border-white/10">
          <div className="w-16 h-16 rounded-full bg-white/5 flex items-center justify-center mb-4">
            <TrendingUp className="w-8 h-8 text-muted-foreground" />
          </div>
          <h3 className="text-xl font-semibold text-white mb-2">No active investments</h3>
          <p className="text-muted-foreground max-w-sm mb-6">
            Your investment portfolio is currently empty. Start trading assets to grow your wealth.
          </p>
          <button onClick={() => setIsDialogOpen(true)} className="flex items-center gap-2 rounded-xl bg-gradient-primary px-6 py-2.5 text-sm font-semibold text-white shadow-glow hover:opacity-95 transition-opacity">
            Explore Markets
          </button>
        </div>
      )}
    </div>
  );
}
