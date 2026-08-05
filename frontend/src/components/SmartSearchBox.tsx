import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Sparkles } from "lucide-react";
import { aiApi } from "../api/ai";
import { extractErrorMessage } from "../api/client";

/**
 * AI Smart Search: lets a guest describe what they want in plain English. We call the AI
 * endpoint to interpret the query into structured filters, then hand off to the regular
 * /search page (which renders results via the same PropertyService.search path on the
 * backend) so there is a single, consistent results-rendering code path.
 */
export function SmartSearchBox() {
  const navigate = useNavigate();
  const [query, setQuery] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!query.trim()) return;
    setLoading(true);
    setError(null);
    try {
      const result = await aiApi.smartSearch(query);
      const f = result.interpretedFilters;
      const params = new URLSearchParams();
      if (f.city) params.set("city", f.city);
      if (f.checkIn) params.set("checkIn", f.checkIn);
      if (f.checkOut) params.set("checkOut", f.checkOut);
      if (f.guests) params.set("guests", String(f.guests));
      if (f.minPrice) params.set("minPrice", String(f.minPrice));
      if (f.maxPrice) params.set("maxPrice", String(f.maxPrice));
      if (f.propertyType) params.set("propertyType", f.propertyType);
      if (f.keyword) params.set("keyword", f.keyword);
      navigate(`/search?${params.toString()}`);
    } catch (err) {
      setError(extractErrorMessage(err, "AI Smart Search is unavailable right now."));
    } finally {
      setLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="w-full">
      <div className="flex items-center gap-2 rounded-full border border-neutral-200 bg-white px-4 py-2.5 shadow-sm focus-within:ring-2 focus-within:ring-brand-400">
        <Sparkles size={18} className="shrink-0 text-brand-600" />
        <input
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          placeholder='Try "cozy beach house in Goa for 4 people under ₹5000"'
          className="w-full border-none bg-transparent p-0 text-sm outline-none placeholder:text-neutral-400"
        />
        <button
          type="submit"
          disabled={loading}
          className="shrink-0 rounded-full bg-neutral-900 px-4 py-1.5 text-xs font-semibold text-white disabled:opacity-50"
        >
          {loading ? "Thinking…" : "Ask AI"}
        </button>
      </div>
      {error && <p className="mt-2 text-xs text-red-600">{error}</p>}
    </form>
  );
}
