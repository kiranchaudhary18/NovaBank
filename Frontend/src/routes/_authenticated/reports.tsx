import { createFileRoute } from "@tanstack/react-router";
import { toast } from "sonner";
import { motion } from "framer-motion";
import { FileText, Download, Calendar } from "lucide-react";
import { useQuery } from "@tanstack/react-query";
import { StatementService } from "@/lib/api/services/statement.service";

export const Route = createFileRoute("/_authenticated/reports")({
  component: ReportsPage,
});

function ReportsPage() {
  const { data: statementPage, isLoading } = useQuery({
    queryKey: ["statements"],
    queryFn: () => StatementService.searchStatements(),
  });

  const reports = statementPage?.content || [];

  return (
    <div className="p-6 lg:p-8 max-w-7xl mx-auto space-y-8">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 mb-8">
        <motion.div
          initial={{ opacity: 0, y: 10 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.4 }}
        >
          <h2 className="text-2xl sm:text-3xl font-display font-bold text-white tracking-tight">
            Generated Reports
          </h2>
          <p className="text-sm text-muted-foreground mt-1">
            Access your financial statements, tax documents, and exports.
          </p>
        </motion.div>

        <motion.div
          initial={{ opacity: 0, scale: 0.95 }}
          animate={{ opacity: 1, scale: 1 }}
          transition={{ duration: 0.4, delay: 0.1 }}
        >
          <button onClick={() => toast.info('Feature Coming Soon', { description: 'This module is currently in development.' })}  className="flex items-center gap-2 rounded-xl bg-gradient-primary px-4 py-2 text-sm font-semibold text-white shadow-glow hover:opacity-95 transition-opacity">
            <FileText className="w-4 h-4" />
            Generate New Report
          </button>
        </motion.div>
      </div>

      {isLoading ? (
        <div className="flex items-center justify-center min-h-[300px]">
          <div className="h-8 w-8 animate-spin rounded-full border-4 border-primary border-t-transparent" />
        </div>
      ) : reports.length === 0 ? (
        <div className="flex flex-col items-center justify-center py-20 text-center glass rounded-2xl border">
          <div className="w-16 h-16 rounded-full bg-white/5 flex items-center justify-center mb-4">
            <FileText className="w-8 h-8 text-muted-foreground" />
          </div>
          <h3 className="text-xl font-semibold text-white mb-2">No reports generated</h3>
          <p className="text-muted-foreground max-w-sm mb-6">
            Generate your first financial statement to see it here.
          </p>
          <button onClick={() => toast.info('Feature Coming Soon', { description: 'This module is currently in development.' })}  className="flex items-center gap-2 rounded-xl bg-gradient-primary px-6 py-2.5 text-sm font-semibold text-white shadow-glow hover:opacity-95 transition-opacity">
            <FileText className="w-4 h-4" />
            Generate Statement
          </button>
        </div>
      ) : (
        <div className="grid lg:grid-cols-2 gap-6">
          {reports.map((report, idx) => (
            <motion.div
              key={report.id}
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.5, delay: idx * 0.1 }}
              className="flex items-center justify-between rounded-2xl border glass p-6 hover:bg-white/[0.04] transition-colors group"
            >
              <div className="flex items-center gap-4">
                <div className="w-12 h-12 rounded-xl bg-white/5 border border-white/10 flex items-center justify-center group-hover:bg-primary/20 group-hover:text-primary transition-colors text-muted-foreground">
                  <FileText className="w-6 h-6" />
                </div>
                <div>
                  <h4 className="font-semibold text-white group-hover:text-primary transition-colors">
                    {report.statementPeriod} Statement
                  </h4>
                  <div className="flex items-center gap-2 text-xs text-muted-foreground mt-1">
                    <span>
                      {new Date(report.statementDate).toLocaleDateString("en-US", {
                        month: "short",
                        day: "numeric",
                        year: "numeric"
                      })}
                    </span>
                    <span>•</span>
                    <span className="font-medium px-1.5 py-0.5 rounded bg-white/10 text-white/80">
                      PDF
                    </span>
                  </div>
                </div>
              </div>

              <a href={report.statementUrl} download className="p-3 rounded-xl bg-white/5 text-muted-foreground hover:bg-white/10 hover:text-white transition-colors">
                <Download className="w-5 h-5" />
              </a>
            </motion.div>
          ))}
        </div>
      )}
    </div>
  );
}
