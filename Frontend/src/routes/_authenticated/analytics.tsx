import { createFileRoute } from "@tanstack/react-router";
import { toast } from "sonner";
import { motion } from "framer-motion";
import { useQuery } from "@tanstack/react-query";
import { AccountService } from "@/lib/api/services/account.service";
import { TransactionService } from "@/lib/api/services/transaction.service";
import {
  Area,
  AreaChart,
  Bar,
  BarChart,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
  PieChart,
  Pie,
  Cell,
} from "recharts";
import { ArrowUpRight, ArrowDownRight, Download } from "lucide-react";

export const Route = createFileRoute("/_authenticated/analytics")({
  component: AnalyticsPage,
});

const pieData = [
  { name: "Software", value: 45 },
  { name: "Payroll", value: 30 },
  { name: "Marketing", value: 15 },
  { name: "Travel", value: 10 },
];
const COLORS = ["#7C5CFF", "#0ea5e9", "#10b981", "#f59e0b", "#ef4444", "#f43f5e"];

function AnalyticsPage() {
  const { data: accounts = [] } = useQuery({
    queryKey: ["accounts"],
    queryFn: () => AccountService.getMyAccounts(),
  });

  const primaryAccount = accounts[0]?.accountNumber;

  const { data: transactions = [] } = useQuery({
    queryKey: ["transactions", primaryAccount],
    queryFn: () => TransactionService.getTransactions(primaryAccount as string),
    enabled: !!primaryAccount,
  });

  // Calculate total income
  const totalIncome = transactions
    .filter((tx) => tx.type === "CREDIT")
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

  const defaultMonths = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul"];
  const dynamicChartData = defaultMonths.map((month) => ({
    month,
    income: monthlyData[month]?.income || 0,
    expenses: monthlyData[month]?.expenses || 0,
  }));

  // Calculate expenses breakdown by category
  const expenseTransactions = transactions.filter((tx) => tx.type === "DEBIT");
  const totalExpenses = expenseTransactions.reduce((acc, curr) => acc + curr.amount, 0);
  
  const expensesByCategory = expenseTransactions.reduce((acc, tx) => {
    const category = tx.category || "Other";
    acc[category] = (acc[category] || 0) + tx.amount;
    return acc;
  }, {} as Record<string, number>);

  const dynamicPieData = Object.entries(expensesByCategory)
    .map(([name, value]) => ({
      name,
      value: totalExpenses > 0 ? Math.round((value / totalExpenses) * 100) : 0,
    }))
    .sort((a, b) => b.value - a.value)
    .slice(0, 5); // top 5 categories
    
  if (dynamicPieData.length === 0) {
    dynamicPieData.push({ name: "No Data", value: 100 });
  }

  return (
    <div className="p-6 lg:p-8 max-w-7xl mx-auto space-y-8">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 mb-8">
        <motion.div
          initial={{ opacity: 0, y: 10 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.4 }}
        >
          <h2 className="text-2xl sm:text-3xl font-display font-bold text-white tracking-tight">
            Financial Analytics
          </h2>
          <p className="text-sm text-muted-foreground mt-1">
            Deep dive into your cash flow, spending patterns, and growth.
          </p>
        </motion.div>
        <motion.div
          initial={{ opacity: 0, scale: 0.95 }}
          animate={{ opacity: 1, scale: 1 }}
          transition={{ duration: 0.4, delay: 0.1 }}
        >
          <button onClick={() => toast.info('Feature Coming Soon', { description: 'This module is currently in development.' })}  className="flex items-center gap-2 rounded-xl bg-white/5 border border-white/10 px-4 py-2 text-sm font-semibold text-white hover:bg-white/10 transition-colors">
            <Download className="w-4 h-4" />
            Export Report
          </button>
        </motion.div>
      </div>

      <div className="grid lg:grid-cols-3 gap-6">
        {/* Main Growth Chart */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5, delay: 0.2 }}
          className="lg:col-span-2 rounded-2xl border glass p-6"
        >
          <div className="flex justify-between items-center mb-6">
            <div>
              <h3 className="text-lg font-semibold text-white">Revenue Growth</h3>
              <div className="flex items-center gap-2 mt-1">
                <span className="text-2xl font-bold text-white">
                  {totalIncome.toLocaleString("en-US", { style: "currency", currency: "USD" })}
                </span>
                {totalIncome > 0 && (
                  <span className="flex items-center text-sm font-medium text-green-400 bg-green-500/10 px-2 py-0.5 rounded-full">
                    <ArrowUpRight className="w-3 h-3 mr-1" />
                    Growth
                  </span>
                )}
              </div>
            </div>
          </div>
          <div className="h-[300px] w-full mt-8">
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={dynamicChartData} margin={{ top: 0, right: 0, left: -20, bottom: 0 }}>
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
                  cursor={{ fill: "#ffffff05" }}
                  contentStyle={{
                    backgroundColor: "#060816",
                    borderColor: "#ffffff20",
                    borderRadius: "8px",
                  }}
                  itemStyle={{ color: "#fff" }}
                />
                <Bar dataKey="income" fill="#7C5CFF" radius={[4, 4, 0, 0]} barSize={30} />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </motion.div>

        {/* Expenses Breakdown Pie */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5, delay: 0.3 }}
          className="rounded-2xl border glass p-6 flex flex-col"
        >
          <div className="mb-6">
            <h3 className="text-lg font-semibold text-white">Expense Breakdown</h3>
            <p className="text-sm text-muted-foreground mt-1">Where your money goes</p>
          </div>
          <div className="flex-1 flex flex-col justify-center items-center relative">
            <div className="h-[200px] w-full">
              <ResponsiveContainer width="100%" height="100%">
                <PieChart>
                  <Pie
                    data={dynamicPieData}
                    cx="50%"
                    cy="50%"
                    innerRadius={60}
                    outerRadius={80}
                    paddingAngle={5}
                    dataKey="value"
                    stroke="none"
                  >
                    {dynamicPieData.map((entry, index) => (
                      <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                    ))}
                  </Pie>
                  <Tooltip
                    contentStyle={{
                      backgroundColor: "#060816",
                      borderColor: "#ffffff20",
                      borderRadius: "8px",
                    }}
                    itemStyle={{ color: "#fff" }}
                  />
                </PieChart>
              </ResponsiveContainer>
            </div>

            <div className="w-full mt-6 space-y-3">
              {dynamicPieData.map((item, i) => (
                <div key={item.name} className="flex items-center justify-between text-sm">
                  <div className="flex items-center gap-2">
                    <div
                      className="w-2.5 h-2.5 rounded-full"
                      style={{ backgroundColor: COLORS[i % COLORS.length] }}
                    />
                    <span className="text-white/80">{item.name}</span>
                  </div>
                  <span className="font-medium text-white">{item.value}%</span>
                </div>
              ))}
            </div>
          </div>
        </motion.div>
      </div>
    </div>
  );
}
