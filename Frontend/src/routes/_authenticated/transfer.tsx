import { createFileRoute } from "@tanstack/react-router";
import { useState } from "react";
import { motion, AnimatePresence } from "framer-motion";
import { useQuery } from "@tanstack/react-query";
import { AccountService } from "@/lib/api/services/account.service";
import { BeneficiaryService } from "@/lib/api/services/beneficiary.service";
import { Send, ArrowRight, CheckCircle2, ChevronRight, User } from "lucide-react";

export const Route = createFileRoute("/_authenticated/transfer")({
  component: TransferPage,
});

function TransferPage() {
  const [step, setStep] = useState(1);
  const [amount, setAmount] = useState("");
  const [selectedAccount, setSelectedAccount] = useState<string | null>(null);
  const [selectedBeneficiary, setSelectedBeneficiary] = useState<string | null>(null);

  const { data: accounts = [], isLoading: accountsLoading } = useQuery({
    queryKey: ["accounts"],
    queryFn: () => AccountService.getMyAccounts(),
  });

  const { data: beneficiariesPage, isLoading: beneficiariesLoading } = useQuery({
    queryKey: ["beneficiaries"],
    queryFn: () => BeneficiaryService.searchBeneficiaries(),
  });
  
  const beneficiaries = beneficiariesPage?.content || [];

  const nextStep = () => setStep((s) => Math.min(s + 1, 4));
  const prevStep = () => setStep((s) => Math.max(s - 1, 1));

  return (
    <div className="p-6 lg:p-8 max-w-4xl mx-auto space-y-8 min-h-[80vh] flex flex-col justify-center">
      {/* Wizard Progress */}
      <div className="flex items-center justify-between relative mb-12">
        <div className="absolute top-1/2 left-0 w-full h-px bg-white/10 -z-10 -translate-y-1/2" />
        <div
          className="absolute top-1/2 left-0 h-px bg-primary -z-10 -translate-y-1/2 transition-all duration-500"
          style={{ width: `${((step - 1) / 3) * 100}%` }}
        />

        {[1, 2, 3, 4].map((i) => (
          <div
            key={i}
            className={`w-10 h-10 rounded-full flex items-center justify-center font-semibold text-sm transition-colors duration-500 ${
              step >= i
                ? "bg-primary text-white shadow-[0_0_20px_rgba(124,92,255,0.4)]"
                : "bg-[#060816] text-muted-foreground border border-white/10"
            }`}
          >
            {step > i ? <CheckCircle2 className="w-5 h-5" /> : i}
          </div>
        ))}
      </div>

      <div className="relative overflow-hidden rounded-3xl border glass min-h-[450px] flex flex-col">
        <div className="flex-1 p-8">
          <AnimatePresence mode="wait">
            {/* Step 1: Select Account */}
            {step === 1 && (
              <motion.div
                key="step1"
                initial={{ opacity: 0, x: 20 }}
                animate={{ opacity: 1, x: 0 }}
                exit={{ opacity: 0, x: -20 }}
                className="space-y-6"
              >
                <div>
                  <h3 className="text-2xl font-bold text-white mb-2">Select From Account</h3>
                  <p className="text-muted-foreground">
                    Choose the account you want to transfer from.
                  </p>
                </div>
                <div className="grid gap-4">
                  {accountsLoading ? (
                    <div className="flex justify-center p-8"><div className="w-8 h-8 rounded-full border-4 border-primary border-t-transparent animate-spin" /></div>
                  ) : accounts.length === 0 ? (
                    <div className="text-center p-8 text-muted-foreground border border-white/10 rounded-xl bg-white/5">No accounts found. Please open an account first.</div>
                  ) : (
                    accounts.map((acc) => (
                      <button
                        key={acc.id}
                        onClick={() => {
                          setSelectedAccount(acc.accountNumber);
                          nextStep();
                        }}
                        className={`flex items-center justify-between p-4 rounded-xl border transition-colors text-left group ${selectedAccount === acc.accountNumber ? "border-primary bg-primary/10" : "border-white/10 bg-white/5 hover:bg-white/10"}`}
                      >
                        <div>
                          <p className="font-semibold text-white">{acc.type} Account</p>
                          <p className="text-sm text-muted-foreground">{acc.accountNumber}</p>
                        </div>
                        <div className="flex items-center gap-4">
                          <p className="font-display font-semibold text-white tracking-tight">
                            {acc.currency === "USD" ? "$" : acc.currency === "EUR" ? "€" : ""}
                            {acc.balance.toLocaleString("en-US", { minimumFractionDigits: 2 })}
                          </p>
                          <ChevronRight className={`w-5 h-5 transition-colors ${selectedAccount === acc.accountNumber ? "text-primary" : "text-muted-foreground group-hover:text-primary"}`} />
                        </div>
                      </button>
                    ))
                  )}
                </div>
              </motion.div>
            )}

            {/* Step 2: Beneficiary */}
            {step === 2 && (
              <motion.div
                key="step2"
                initial={{ opacity: 0, x: 20 }}
                animate={{ opacity: 1, x: 0 }}
                exit={{ opacity: 0, x: -20 }}
                className="space-y-6"
              >
                <div>
                  <h3 className="text-2xl font-bold text-white mb-2">Select Beneficiary</h3>
                  <p className="text-muted-foreground">Who are you sending money to?</p>
                </div>

                <div className="grid gap-4 sm:grid-cols-2">
                  {beneficiariesLoading ? (
                    <div className="flex justify-center p-8 col-span-full"><div className="w-8 h-8 rounded-full border-4 border-primary border-t-transparent animate-spin" /></div>
                  ) : (
                    beneficiaries.map((ben) => (
                      <button
                        key={ben.id}
                        onClick={() => {
                          setSelectedBeneficiary(ben.nickname || ben.name);
                          nextStep();
                        }}
                        className={`flex items-center gap-4 p-4 rounded-xl border transition-colors text-left ${selectedBeneficiary === (ben.nickname || ben.name) ? "border-primary bg-primary/10" : "border-white/10 bg-white/5 hover:bg-white/10"}`}
                      >
                        <div className="w-12 h-12 rounded-full bg-primary/20 text-primary flex items-center justify-center shrink-0 font-bold">
                          {ben.nickname?.substring(0, 2).toUpperCase() || ben.name.substring(0, 2).toUpperCase()}
                        </div>
                        <div>
                          <p className="font-semibold text-white">{ben.nickname || ben.name}</p>
                          <p className="text-sm text-muted-foreground">••• {ben.accountNumber.slice(-4)}</p>
                        </div>
                      </button>
                    ))
                  )}

                  <button
                    onClick={nextStep}
                    className="flex items-center gap-4 p-4 rounded-xl border border-white/10 bg-white/5 hover:bg-white/10 transition-colors text-left border-dashed hover:border-primary/50 hover:text-primary group"
                  >
                    <div className="w-12 h-12 rounded-full border border-dashed border-white/20 flex items-center justify-center shrink-0 group-hover:border-primary/50 text-muted-foreground group-hover:text-primary transition-colors">
                      +
                    </div>
                    <div>
                      <p className="font-semibold text-white transition-colors group-hover:text-primary">
                        New Beneficiary
                      </p>
                      <p className="text-sm text-muted-foreground">Add bank details</p>
                    </div>
                  </button>
                </div>
              </motion.div>
            )}

            {/* Step 3: Amount */}
            {step === 3 && (
              <motion.div
                key="step3"
                initial={{ opacity: 0, x: 20 }}
                animate={{ opacity: 1, x: 0 }}
                exit={{ opacity: 0, x: -20 }}
                className="space-y-8 flex flex-col items-center justify-center h-full py-12"
              >
                <div className="text-center">
                  <p className="text-muted-foreground mb-4">Enter Amount to send</p>
                  <div className="flex items-center justify-center gap-2">
                    <span className="text-4xl sm:text-6xl font-display font-medium text-white/50">
                      $
                    </span>
                    <input
                      type="text"
                      placeholder="0.00"
                      value={amount}
                      onChange={(e) => setAmount(e.target.value)}
                      className="bg-transparent text-5xl sm:text-7xl font-display font-bold text-white tracking-tighter outline-none w-[200px] sm:w-[300px] text-center"
                      autoFocus
                    />
                  </div>
                </div>

                <div className="w-full max-w-sm">
                  <label className="text-sm font-medium text-muted-foreground mb-2 block">
                    Remarks (Optional)
                  </label>
                  <input
                    type="text"
                    placeholder="e.g. Invoice #2049"
                    className="w-full bg-white/5 border border-white/10 rounded-xl px-4 py-3 text-white placeholder:text-white/30 focus:outline-none focus:border-primary/50 transition-colors"
                  />
                </div>
              </motion.div>
            )}

            {/* Step 4: Success */}
            {step === 4 && (
              <motion.div
                key="step4"
                initial={{ opacity: 0, scale: 0.95 }}
                animate={{ opacity: 1, scale: 1 }}
                className="flex flex-col items-center justify-center h-full py-16 space-y-6 text-center"
              >
                <motion.div
                  initial={{ scale: 0 }}
                  animate={{ scale: 1 }}
                  transition={{ type: "spring", bounce: 0.5, delay: 0.2 }}
                  className="w-24 h-24 rounded-full bg-green-500/20 text-green-400 flex items-center justify-center shadow-[0_0_40px_rgba(74,222,128,0.2)]"
                >
                  <CheckCircle2 className="w-12 h-12" />
                </motion.div>

                <div>
                  <h3 className="text-3xl font-display font-bold text-white mb-2">
                    Transfer Successful!
                  </h3>
                  <p className="text-muted-foreground max-w-md mx-auto">
                    Your transfer of <strong className="text-white">${amount || "0.00"}</strong> to{" "}
                    <strong>{selectedBeneficiary || "the recipient"}</strong> has been processed successfully.
                  </p>
                </div>

                <div className="pt-8">
                  <button
                    onClick={() => {
                      setStep(1);
                      setAmount("");
                    }}
                    className="rounded-xl bg-white/5 border border-white/10 px-6 py-3 font-semibold text-white hover:bg-white/10 transition-colors"
                  >
                    Make another transfer
                  </button>
                </div>
              </motion.div>
            )}
          </AnimatePresence>
        </div>

        {/* Wizard Footer Controls */}
        {step < 4 && (
          <div className="p-6 border-t border-white/10 flex items-center justify-between bg-black/20">
            <button
              onClick={prevStep}
              disabled={step === 1}
              className="px-6 py-2.5 rounded-xl font-medium text-white disabled:opacity-30 hover:bg-white/5 transition-colors"
            >
              Back
            </button>

            {step === 3 && (
              <button
                onClick={nextStep}
                disabled={!amount}
                className="flex items-center gap-2 rounded-xl bg-gradient-primary px-6 py-2.5 font-semibold text-white shadow-glow disabled:opacity-50 disabled:shadow-none hover:opacity-95 transition-all"
              >
                <Send className="w-4 h-4" />
                Confirm Transfer
              </button>
            )}

            {step < 3 && (
              <button
                onClick={nextStep}
                className="flex items-center gap-2 rounded-xl bg-white text-black px-6 py-2.5 font-semibold hover:bg-white/90 transition-colors"
              >
                Continue
                <ArrowRight className="w-4 h-4" />
              </button>
            )}
          </div>
        )}
      </div>
    </div>
  );
}
