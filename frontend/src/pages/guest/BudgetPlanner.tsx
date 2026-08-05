import { useState } from "react";
import { useMutation } from "@tanstack/react-query";
import { Sparkles } from "lucide-react";
import { aiApi } from "../../api/ai";
import { extractErrorMessage } from "../../api/client";
import { ErrorBanner } from "../../components/ErrorBanner";

export function BudgetPlanner() {
  const [destination, setDestination] = useState("");
  const [travelers, setTravelers] = useState(2);
  const [nights, setNights] = useState(3);
  const [totalBudget, setTotalBudget] = useState(20000);
  const [currency, setCurrency] = useState("INR");

  const planMutation = useMutation({
    mutationFn: () => aiApi.budgetPlanner({ destination, travelers, nights, totalBudget, currency }),
  });

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    planMutation.mutate();
  };

  return (
    <div className="mx-auto max-w-3xl px-4 py-8 sm:px-6">
      <div className="flex items-center gap-2">
        <Sparkles className="text-brand-600" />
        <h1 className="text-2xl font-bold text-neutral-900">AI Budget Planner</h1>
      </div>
      <p className="mt-1 text-neutral-500">
        Get a realistic budget breakdown grounded in real StaySmart AI pricing for your destination.
      </p>

      <form onSubmit={handleSubmit} className="mt-6 grid grid-cols-1 gap-4 rounded-2xl border border-neutral-200 bg-white p-5 sm:grid-cols-2">
        <div className="sm:col-span-2">
          <label className="mb-1 block text-sm font-medium text-neutral-700">Destination</label>
          <input
            required
            value={destination}
            onChange={(e) => setDestination(e.target.value)}
            placeholder="e.g. Manali, India"
            className="w-full rounded-lg border border-neutral-300 px-3 py-2 text-sm"
          />
        </div>
        <div>
          <label className="mb-1 block text-sm font-medium text-neutral-700">Travelers</label>
          <input
            type="number"
            min={1}
            value={travelers}
            onChange={(e) => setTravelers(Number(e.target.value))}
            className="w-full rounded-lg border border-neutral-300 px-3 py-2 text-sm"
          />
        </div>
        <div>
          <label className="mb-1 block text-sm font-medium text-neutral-700">Nights</label>
          <input
            type="number"
            min={1}
            value={nights}
            onChange={(e) => setNights(Number(e.target.value))}
            className="w-full rounded-lg border border-neutral-300 px-3 py-2 text-sm"
          />
        </div>
        <div>
          <label className="mb-1 block text-sm font-medium text-neutral-700">Total budget</label>
          <input
            type="number"
            min={1}
            value={totalBudget}
            onChange={(e) => setTotalBudget(Number(e.target.value))}
            className="w-full rounded-lg border border-neutral-300 px-3 py-2 text-sm"
          />
        </div>
        <div>
          <label className="mb-1 block text-sm font-medium text-neutral-700">Currency</label>
          <select
            value={currency}
            onChange={(e) => setCurrency(e.target.value)}
            className="w-full rounded-lg border border-neutral-300 px-3 py-2 text-sm"
          >
            <option value="INR">INR (₹)</option>
            <option value="USD">USD ($)</option>
            <option value="EUR">EUR (€)</option>
          </select>
        </div>
        <div className="sm:col-span-2">
          <button
            type="submit"
            disabled={planMutation.isPending}
            className="w-full rounded-full bg-brand-600 py-2.5 text-sm font-semibold text-white hover:bg-brand-700 disabled:opacity-50"
          >
            {planMutation.isPending ? "Building your budget…" : "Generate budget plan"}
          </button>
        </div>
      </form>

      {planMutation.isError && (
        <div className="mt-4">
          <ErrorBanner message={extractErrorMessage(planMutation.error, "AI Budget Planner is unavailable right now.")} />
        </div>
      )}

      {planMutation.data && (
        <div className="mt-6 rounded-2xl border border-brand-100 bg-brand-50/50 p-5">
          {planMutation.data.avgNightlyPriceReference != null && planMutation.data.avgNightlyPriceReference > 0 && (
            <p className="mb-3 text-xs font-medium text-neutral-500">
              Based on an average nightly price of ₹{planMutation.data.avgNightlyPriceReference.toLocaleString()} for{" "}
              {planMutation.data.destination} on StaySmart AI.
            </p>
          )}
          <div className="whitespace-pre-line text-sm leading-relaxed text-neutral-700">{planMutation.data.plan}</div>
        </div>
      )}
    </div>
  );
}
