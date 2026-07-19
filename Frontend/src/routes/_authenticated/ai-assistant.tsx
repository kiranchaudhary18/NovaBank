import { createFileRoute } from "@tanstack/react-router";
import { useState, useRef } from "react";
import { motion, AnimatePresence } from "framer-motion";
import { Send, Bot, Sparkles, Loader2 } from "lucide-react";
import { useCurrentUser } from "@/lib/api/hooks/use-auth";

export const Route = createFileRoute("/_authenticated/ai-assistant")({
  component: AIAssistantPage,
});

interface Message {
  id: string;
  role: "user" | "assistant";
  content: string;
  timestamp: string;
}

const suggestions = [
  "Analyze my software subscriptions",
  "How much did I spend on AWS last month?",
  "What is my projected cash flow for Q3?",
  "Categorize my recent uncategorized transactions",
];

function AIAssistantPage() {
  const [messages, setMessages] = useState<Message[]>([
    {
      id: "1",
      role: "assistant",
      content:
        "Good morning! I'm Nova, your AI financial assistant. I've noticed your software expenses increased by 12% this month. Would you like me to analyze your subscriptions?",
      timestamp: new Date().toISOString(),
    },
  ]);
  const [input, setInput] = useState("");
  const [isTyping, setIsTyping] = useState(false);
  const { data: currentUser } = useCurrentUser();

  const handleSend = (text: string = input) => {
    if (!text.trim()) return;

    const userMsg: Message = { 
      id: Date.now().toString(), 
      role: "user", 
      content: text,
      timestamp: new Date().toISOString()
    };
    setMessages((prev) => [...prev, userMsg]);
    setInput("");
    setIsTyping(true);

    setTimeout(() => {
      setIsTyping(false);
      setMessages((prev) => [
        ...prev,
        {
          id: (Date.now() + 1).toString(),
          role: "assistant",
          content:
            "I've analyzed your AWS and Azure billing. You have 3 idle EC2 instances and an unattached EBS volume that cost you $450 last month. I can automatically generate a termination script for your DevOps team if you'd like.",
          timestamp: new Date().toISOString(),
        },
      ]);
    }, 1500);
  };

  return (
    <div className="p-6 lg:p-8 max-w-5xl mx-auto h-[calc(100vh-4rem)] flex flex-col">
      <div className="mb-6">
        <motion.div
          initial={{ opacity: 0, y: 10 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.4 }}
          className="flex items-center gap-3"
        >
          <div className="p-2 rounded-xl bg-primary/20 text-primary">
            <Sparkles className="w-6 h-6" />
          </div>
          <div>
            <h2 className="text-2xl font-display font-bold text-white tracking-tight">Nova AI</h2>
            <p className="text-sm text-muted-foreground mt-1">
              Your enterprise financial intelligence.
            </p>
          </div>
        </motion.div>
      </div>

      <div className="flex-1 overflow-hidden flex flex-col relative rounded-3xl border glass shadow-2xl">
        <div className="flex-1 overflow-y-auto scrollbar-none p-6 space-y-6">
          {messages.map((msg, idx) => (
            <motion.div
              key={msg.id}
              initial={{ opacity: 0, y: 10 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.3 }}
              className={`flex gap-4 max-w-[80%] ${msg.role === "user" ? "ml-auto flex-row-reverse" : ""}`}
            >
              <div
                className={`w-8 h-8 rounded-full flex items-center justify-center shrink-0 ${
                  msg.role === "user" ? "bg-white/10 p-0" : "bg-primary/20 text-primary"
                }`}
              >
                {msg.role === "user" ? (
                  <div className="w-8 h-8 rounded-full border border-white/10 shrink-0 bg-primary/20 flex items-center justify-center text-primary font-bold text-xs mt-1 overflow-hidden">
                    {currentUser?.fullName?.charAt(0).toUpperCase() || "U"}
                  </div>
                ) : (
                  <Bot className="w-5 h-5" />
                )}
              </div>
              <div
                className={`p-4 rounded-2xl ${
                  msg.role === "user"
                    ? "bg-primary text-white rounded-tr-sm"
                    : "bg-white/5 border border-white/10 text-white/90 rounded-tl-sm leading-relaxed"
                }`}
              >
                {msg.content}
              </div>
            </motion.div>
          ))}
          {isTyping && (
            <motion.div
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              className="flex gap-4 max-w-[80%]"
            >
              <div className="w-8 h-8 rounded-full bg-primary/20 text-primary flex items-center justify-center shrink-0">
                <Bot className="w-5 h-5" />
              </div>
              <div className="p-4 rounded-2xl bg-white/5 border border-white/10 text-white/90 rounded-tl-sm flex items-center gap-2">
                <Loader2 className="w-4 h-4 animate-spin text-primary" />
                <span className="text-sm text-muted-foreground">Nova is analyzing...</span>
              </div>
            </motion.div>
          )}
        </div>

        {/* Input Area */}
        <div className="p-4 border-t border-white/10 bg-black/20">
          <div className="mb-4 flex flex-wrap gap-2">
            {suggestions.map((sug, i) => (
              <button
                key={i}
                onClick={() => handleSend(sug)}
                className="text-xs px-3 py-1.5 rounded-full border border-white/10 bg-white/5 text-white/70 hover:bg-white/10 hover:text-white transition-colors"
              >
                {sug}
              </button>
            ))}
          </div>

          <div className="relative flex items-center">
            <input
              type="text"
              value={input}
              onChange={(e) => setInput(e.target.value)}
              onKeyDown={(e) => e.key === "Enter" && handleSend()}
              placeholder="Ask Nova anything about your finances..."
              className="w-full bg-white/5 border border-white/10 rounded-xl pl-4 pr-12 py-4 text-white placeholder:text-muted-foreground focus:outline-none focus:border-primary/50 transition-colors shadow-inner"
            />
            <button
              onClick={() => handleSend()}
              disabled={!input.trim()}
              className="absolute right-2 p-2 rounded-lg bg-primary text-white disabled:opacity-50 hover:opacity-90 transition-opacity"
            >
              <Send className="w-4 h-4" />
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
