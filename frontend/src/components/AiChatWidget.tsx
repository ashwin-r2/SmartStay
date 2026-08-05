import { useEffect, useRef, useState } from "react";
import { MessageCircle, Send, Sparkles, X } from "lucide-react";
import { aiApi } from "../api/ai";
import { extractErrorMessage } from "../api/client";
import type { ChatMessageDto } from "../types";

const SESSION_KEY = "staysmart_chat_session_id";

/** Floating AI concierge chat widget, available site-wide once a user is logged in. */
export function AiChatWidget() {
  const [open, setOpen] = useState(false);
  const [sessionId, setSessionId] = useState<string | null>(() => localStorage.getItem(SESSION_KEY));
  const [messages, setMessages] = useState<ChatMessageDto[]>([]);
  const [input, setInput] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [historyLoaded, setHistoryLoaded] = useState(false);
  const scrollRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (open && sessionId && !historyLoaded) {
      aiApi
        .chatHistory(sessionId)
        .then(setMessages)
        .catch(() => undefined)
        .finally(() => setHistoryLoaded(true));
    }
  }, [open, sessionId, historyLoaded]);

  useEffect(() => {
    scrollRef.current?.scrollTo({ top: scrollRef.current.scrollHeight, behavior: "smooth" });
  }, [messages, loading]);

  const handleSend = async () => {
    const text = input.trim();
    if (!text || loading) return;
    setInput("");
    setError(null);
    setMessages((prev) => [...prev, { role: "USER", content: text, createdAt: new Date().toISOString() }]);
    setLoading(true);
    try {
      const res = await aiApi.chat(text, sessionId ?? undefined);
      if (res.sessionId !== sessionId) {
        setSessionId(res.sessionId);
        localStorage.setItem(SESSION_KEY, res.sessionId);
      }
      setMessages((prev) => [...prev, { role: "ASSISTANT", content: res.reply, createdAt: new Date().toISOString() }]);
    } catch (err) {
      setError(extractErrorMessage(err, "The AI assistant is temporarily unavailable."));
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="fixed bottom-5 right-5 z-50">
      {open ? (
        <div className="flex h-[28rem] w-80 flex-col overflow-hidden rounded-2xl border border-neutral-200 bg-white shadow-xl sm:w-96">
          <div className="flex items-center justify-between bg-neutral-900 px-4 py-3 text-white">
            <span className="flex items-center gap-1.5 text-sm font-semibold">
              <Sparkles size={15} /> StaySmart AI Assistant
            </span>
            <button onClick={() => setOpen(false)} aria-label="Close chat">
              <X size={16} />
            </button>
          </div>

          <div ref={scrollRef} className="scrollbar-thin flex-1 space-y-3 overflow-y-auto p-3">
            {messages.length === 0 && (
              <p className="mt-4 text-center text-xs text-neutral-400">
                Ask me about your bookings, amenities, or general travel tips!
              </p>
            )}
            {messages.map((m, i) => (
              <div key={i} className={`flex ${m.role === "USER" ? "justify-end" : "justify-start"}`}>
                <div
                  className={`max-w-[85%] whitespace-pre-line rounded-2xl px-3 py-2 text-sm ${
                    m.role === "USER" ? "bg-brand-600 text-white" : "bg-neutral-100 text-neutral-800"
                  }`}
                >
                  {m.content}
                </div>
              </div>
            ))}
            {loading && (
              <div className="flex justify-start">
                <div className="rounded-2xl bg-neutral-100 px-3 py-2 text-sm text-neutral-500">Thinking…</div>
              </div>
            )}
            {error && <p className="text-center text-xs text-red-600">{error}</p>}
          </div>

          <div className="flex items-center gap-2 border-t border-neutral-200 p-2.5">
            <input
              value={input}
              onChange={(e) => setInput(e.target.value)}
              onKeyDown={(e) => e.key === "Enter" && handleSend()}
              placeholder="Type a message…"
              className="flex-1 rounded-full border border-neutral-300 px-3 py-2 text-sm outline-none focus:border-brand-400"
            />
            <button
              onClick={handleSend}
              disabled={loading || !input.trim()}
              aria-label="Send message"
              className="flex h-9 w-9 shrink-0 items-center justify-center rounded-full bg-brand-600 text-white disabled:opacity-40"
            >
              <Send size={15} />
            </button>
          </div>
        </div>
      ) : (
        <button
          onClick={() => setOpen(true)}
          aria-label="Open AI chat assistant"
          className="flex h-14 w-14 items-center justify-center rounded-full bg-brand-600 text-white shadow-lg hover:bg-brand-700"
        >
          <MessageCircle size={24} />
        </button>
      )}
    </div>
  );
}
