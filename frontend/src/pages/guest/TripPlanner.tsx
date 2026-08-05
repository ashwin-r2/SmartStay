import { useState } from "react";
import { useMutation } from "@tanstack/react-query";
import { Sparkles } from "lucide-react";
import { aiApi } from "../../api/ai";
import { extractErrorMessage } from "../../api/client";
import { ErrorBanner } from "../../components/ErrorBanner";

export function TripPlanner() {
  const [destination, setDestination] = useState("");
  const [startDate, setStartDate] = useState("");
  const [endDate, setEndDate] = useState("");
  const [travelers, setTravelers] = useState(2);
  const [interests, setInterests] = useState("");
  const [budgetLevel, setBudgetLevel] = useState("mid-range");

  const planMutation = useMutation({
    mutationFn: () => aiApi.tripPlanner({ destination, startDate, endDate, travelers, interests, budgetLevel }),
  });

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    planMutation.mutate();
  };

  return (
    <div className="mx-auto max-w-3xl px-4 py-8 sm:px-6">
      <div className="flex items-center gap-2">
        <Sparkles className="text-brand-600" />
        <h1 className="text-2xl font-bold text-neutral-900">AI Trip Planner</h1>
      </div>
      <p className="mt-1 text-neutral-500">Tell us about your trip and get a day-by-day itinerary in seconds.</p>

      <form onSubmit={handleSubmit} className="mt-6 grid grid-cols-1 gap-4 rounded-2xl border border-neutral-200 bg-white p-5 sm:grid-cols-2">
        <div className="sm:col-span-2">
          <label className="mb-1 block text-sm font-medium text-neutral-700">Destination</label>
          <input
            required
            value={destination}
            onChange={(e) => setDestination(e.target.value)}
            placeholder="e.g. Goa, India"
            className="w-full rounded-lg border border-neutral-300 px-3 py-2 text-sm"
          />
        </div>
        <div>
          <label className="mb-1 block text-sm font-medium text-neutral-700">Start date</label>
          <input
            required
            type="date"
            value={startDate}
            onChange={(e) => setStartDate(e.target.value)}
            className="w-full rounded-lg border border-neutral-300 px-3 py-2 text-sm"
          />
        </div>
        <div>
          <label className="mb-1 block text-sm font-medium text-neutral-700">End date</label>
          <input
            required
            type="date"
            value={endDate}
            onChange={(e) => setEndDate(e.target.value)}
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
          <label className="mb-1 block text-sm font-medium text-neutral-700">Budget level</label>
          <select
            value={budgetLevel}
            onChange={(e) => setBudgetLevel(e.target.value)}
            className="w-full rounded-lg border border-neutral-300 px-3 py-2 text-sm"
          >
            <option value="budget">Budget</option>
            <option value="mid-range">Mid-range</option>
            <option value="luxury">Luxury</option>
          </select>
        </div>
        <div className="sm:col-span-2">
          <label className="mb-1 block text-sm font-medium text-neutral-700">Interests (optional)</label>
          <input
            value={interests}
            onChange={(e) => setInterests(e.target.value)}
            placeholder="e.g. beaches, food, nightlife, history"
            className="w-full rounded-lg border border-neutral-300 px-3 py-2 text-sm"
          />
        </div>
        <div className="sm:col-span-2">
          <button
            type="submit"
            disabled={planMutation.isPending}
            className="w-full rounded-full bg-brand-600 py-2.5 text-sm font-semibold text-white hover:bg-brand-700 disabled:opacity-50"
          >
            {planMutation.isPending ? "Planning your trip…" : "Generate itinerary"}
          </button>
        </div>
      </form>

      {planMutation.isError && (
        <div className="mt-4">
          <ErrorBanner message={extractErrorMessage(planMutation.error, "AI Trip Planner is unavailable right now.")} />
        </div>
      )}

      {planMutation.data && (
        <div className="mt-6 whitespace-pre-line rounded-2xl border border-brand-100 bg-brand-50/50 p-5 text-sm leading-relaxed text-neutral-700">
          {planMutation.data.itinerary}
        </div>
      )}
    </div>
  );
}
