import { createFileRoute } from "@tanstack/react-router";
import { motion } from "framer-motion";
import {
  ArrowUpRight,
  ArrowDownRight,
  Wallet,
  TrendingUp,
  DollarSign,
  Activity,
  Sparkles,
} from "lucide-react";
import { Area, AreaChart, ResponsiveContainer, Tooltip, XAxis, YAxis } from "recharts";
import { useQuery } from "@tanstack/react-query";
import { AccountService } from "@/lib/api/services/account.service";
import { TransactionService } from "@/lib/api/services/transaction.service";
import { useCurrentUser } from "@/lib/api/hooks/use-auth";
import { Link } from "@tanstack/react-router";

export const Route = createFileRoute("/_authenticated/dashboard")({
  component: DashboardPage,
});

function DashboardPage() {
  const { data: user } = useCurrentUser();
  
  const { data: accounts = [] } = useQuery({
    queryKey: ["accounts"],
    queryFn: () => AccountService.getMyAccounts(),
  });

  const primaryAccount = accounts[0]?.accountNumber;
  const totalBalance = accounts.reduce((acc, curr) => acc + curr.balance, 0);

  const { data: transactions = [] } = useQuery({
    queryKey: ["transactions", primaryAccount],
    queryFn: () => TransactionService.getTransactions(primaryAccount as string),
    enabled: !!primaryAccount,
  });

  const totalIncome = transactions
    .filter((tx) => tx.type === "CREDIT")
    .reduce((acc, curr) => acc + curr.amount, 0);

  const totalExpenses = transactions
    .filter((tx) => tx.type === "DEBIT")
    .reduce((acc, curr) => acc + curr.amount, 0);

  // Group transactions by month for the chart
  const monthlyData = transactions.reduce((acc, tx) => {
    const date = new Date(tx.createdAt);
    const month = date.toLocaleString('default', { month: 'short' });
    if (!acc[month]) acc[month] = { income: 0, expenses: 0 };
    if (tx.type === "CREDIT") acc[month].income += tx.amount;
    else acc[month].expenses += tx.amount;
    return acc;
  }, {} as Record<string, { income: number; expenses: number }>);

  // Default months if no data exists
  const defaultMonths = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul"];
  const dynamicChartData = defaultMonths.map((month) => ({
    month,
    income: monthlyData[month]?.income || 0,
    expenses: monthlyData[month]?.expenses || 0,
  }));

  return (
    <div className="p-6 lg:p-8 max-w-7xl mx-auto space-y-8">
      {/* Welcome Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <motion.div
          initial={{ opacity: 0, y: 10 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.4 }}
        >
          <h2 className="text-2xl sm:text-3xl font-display font-bold text-white tracking-tight">
            Good morning, {user?.fullName?.split(" ")[0] || "User"}
          </h2>
          <p className="text-sm text-muted-foreground mt-1">
            Here's what's happening with your accounts today.
          </p>
        </motion.div>

        <motion.div
          initial={{ opacity: 0, scale: 0.95 }}
          animate={{ opacity: 1, scale: 1 }}
          transition={{ duration: 0.4, delay: 0.1 }}
          className="flex items-center gap-3"
        >
          <Link 
            to="/reports"
            className="rounded-xl bg-white/5 border border-white/10 px-4 py-2 text-sm font-semibold text-white hover:bg-white/10 transition-colors"
          >
            Download Report
          </Link>
          <Link 
            to="/transfer"
            className="rounded-xl bg-gradient-primary px-4 py-2 text-sm font-semibold text-white shadow-glow hover:opacity-95 transition-opacity"
          >
            Send Money
          </Link>
        </motion.div>
      </div>

      {/* Stats Grid */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
        <StatCard
          title="Total Balance"
          value={totalBalance > 0 ? totalBalance.toLocaleString("en-US", { style: "currency", currency: "USD" }) : "$0.00"}
          trend="+2.5%"
          isUp={true}
          icon={Wallet}
          delay={0.1}
        />
        <StatCard
          title="Total Income (All Time)"
          value={totalIncome.toLocaleString("en-US", { style: "currency", currency: "USD" })}
          trend={totalIncome > 0 ? "+0.0%" : ""}
          isUp={totalIncome > 0 ? true : null}
          icon={TrendingUp}
          delay={0.2}
        />
        <StatCard
          title="Total Expenses (All Time)"
          value={totalExpenses.toLocaleString("en-US", { style: "currency", currency: "USD" })}
          trend={totalExpenses > 0 ? "-0.0%" : ""}
          isUp={totalExpenses > 0 ? false : null}
          icon={DollarSign}
          delay={0.3}
        />
        <StatCard
          title="Active Loans"
          value="$0.00"
          trend="No active loans"
          isUp={null}
          icon={Activity}
          delay={0.4}
        />
      </div>

      <div className="grid lg:grid-cols-3 gap-6">
        {/* Main Chart */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5, delay: 0.3 }}
          className="lg:col-span-2 rounded-2xl border glass p-6"
        >
          <div className="flex items-center justify-between mb-8">
            <div>
              <h3 className="text-lg font-semibold text-white">Cash Flow</h3>
              <p className="text-sm text-muted-foreground">Income vs Expenses over time</p>
            </div>
            <select className="bg-white/5 border border-white/10 text-white text-sm rounded-lg px-3 py-1.5 outline-none focus:ring-2 focus:ring-primary/50">
              <option>Last 7 months</option>
              <option>This Year</option>
            </select>
          </div>
          <div className="h-[300px] w-full">
            <ResponsiveContainer width="100%" height="100%">
              <AreaChart data={dynamicChartData} margin={{ top: 0, right: 0, left: -20, bottom: 0 }}>
                <defs>
                  <linearGradient id="colorIncome" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="5%" stopColor="#7C5CFF" stopOpacity={0.3} />
                    <stop offset="95%" stopColor="#7C5CFF" stopOpacity={0} />
                  </linearGradient>
                  <linearGradient id="colorExpense" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="5%" stopColor="#ef4444" stopOpacity={0.3} />
                    <stop offset="95%" stopColor="#ef4444" stopOpacity={0} />
                  </linearGradient>
                </defs>
                <XAxis
                  dataKey="month"
                  stroke="#ffffff40"
                  fontSize={12}
                  tickLine={false}
                  axisLine={false}
                />
                <YAxis
                  stroke="#ffffff40"
                  fontSize={12}
                  tickLine={false}
                  axisLine={false}
                  tickFormatter={(value) => `$${value / 1000}k`}
                />
                <Tooltip
                  contentStyle={{
                    backgroundColor: "#060816",
                    borderColor: "#ffffff20",
                    borderRadius: "8px",
                  }}
                  itemStyle={{ color: "#fff" }}
                />
                <Area
                  type="monotone"
                  dataKey="income"
                  stroke="#7C5CFF"
                  strokeWidth={3}
                  fillOpacity={1}
                  fill="url(#colorIncome)"
                />
                <Area
                  type="monotone"
                  dataKey="expenses"
                  stroke="#ef4444"
                  strokeWidth={3}
                  fillOpacity={1}
                  fill="url(#colorExpense)"
                />
              </AreaChart>
            </ResponsiveContainer>
          </div>
        </motion.div>

        {/* AI Insights & Recent Transactions */}
        <div className="space-y-6">
          <motion.div
            initial={{ opacity: 0, x: 20 }}
            animate={{ opacity: 1, x: 0 }}
            transition={{ duration: 0.5, delay: 0.4 }}
            className="rounded-2xl border border-primary/20 bg-gradient-to-br from-primary/10 to-primary/5 p-6 backdrop-blur-md relative overflow-hidden"
          >
            <div className="absolute top-0 right-0 p-4 opacity-10">
              <Sparkles className="w-24 h-24 text-primary" />
            </div>
            <div className="relative z-10">
              <div className="flex items-center gap-2 mb-3">
                <Sparkles className="w-5 h-5 text-primary" />
                <h3 className="font-semibold text-white">AI Insight</h3>
              </div>
              <p className="text-sm text-white/80 leading-relaxed mb-4">
                {transactions.length > 0
                  ? "You have " + transactions.length + " recent transactions. " + 
                    (totalExpenses > 0
                      ? "Your total expenditure is " + totalExpenses.toLocaleString("en-US", { style: "currency", currency: "USD" }) + "."
                      : "You haven't spent anything yet.")
                  : "Start using your account to receive personalized AI financial insights and recommendations."}
              </p>
              <button className="text-sm font-semibold text-primary hover:text-primary-light transition-colors">
                View detailed analysis &rarr;
              </button>
            </div>
          </motion.div>

          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.5, delay: 0.5 }}
            className="rounded-2xl border glass p-6"
          >
            <div className="flex items-center justify-between mb-6">
              <h3 className="text-lg font-semibold text-white">Recent Activity</h3>
              <Link to="/transactions" className="text-sm text-muted-foreground hover:text-white transition-colors">
                View All
              </Link>
            </div>
            <div className="space-y-5">
              {transactions.slice(0, 4).map((tx) => (
                <div key={tx.id} className="flex items-center justify-between">
                  <div className="flex items-center gap-3">
                    <div className="h-10 w-10 flex items-center justify-center rounded-full bg-white/5 p-2 shrink-0 border border-white/10">
                      {tx.merchant?.[0]?.toUpperCase() || "T"}
                    </div>
                    <div>
                      <p className="text-sm font-medium text-white line-clamp-1">
                        {tx.merchant}
                      </p>
                      <p className="text-xs text-muted-foreground">{tx.category}</p>
                    </div>
                  </div>
                  <div
                    className={`text-sm font-semibold ${tx.type === "CREDIT" ? "text-green-400" : "text-white"}`}
                  >
                    {tx.type === "CREDIT" ? "+" : "-"}
                    {tx.amount.toLocaleString("en-US", { style: "currency", currency: "USD" })}
                  </div>
                </div>
              ))}
            </div>
          </motion.div>
        </div>
      </div>
    </div>
  );
}

// eslint-disable-next-line @typescript-eslint/no-explicit-any
function StatCard({ title, value, trend, isUp, icon: Icon, delay }: any) {
  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.4, delay }}
      className="rounded-2xl border glass p-6 hover:bg-white/[0.04] transition-colors"
    >
      <div className="flex items-center justify-between mb-4">
        <h3 className="text-sm font-medium text-muted-foreground">{title}</h3>
        <div className="p-2 bg-white/5 rounded-lg">
          <Icon className="h-5 w-5 text-white/70" />
        </div>
      </div>
      <div className="space-y-2">
        <h4 className="text-2xl font-display font-bold text-white tracking-tight">{value}</h4>
        {trend && (
          <div className="flex items-center gap-1.5 text-sm">
            {isUp === true && <ArrowUpRight className="h-4 w-4 text-green-400" />}
            {isUp === false && <ArrowDownRight className="h-4 w-4 text-red-400" />}
            <span
              className={
                isUp === true
                  ? "text-green-400"
                  : isUp === false
                    ? "text-red-400"
                    : "text-muted-foreground"
              }
            >
              {trend}
            </span>
          </div>
        )}
      </div>
    </motion.div>
  );
}
