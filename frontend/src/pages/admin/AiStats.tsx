import { useQuery } from "@tanstack/react-query";
import { adminApi } from "../../api/admin";
import { LoadingSpinner } from "../../components/LoadingSpinner";
import { ErrorBanner } from "../../components/ErrorBanner";
import { StatTile } from "../../components/StatTile";
import { BarChart } from "../../components/BarChart";
import { extractErrorMessage } from "../../api/client";

const FEATURE_LABELS: Record<string, string> = {
  RECOMMENDATION: "Recommendations",
  REVIEW_SUMMARY: "Review Summary",
  CHAT_ASSISTANT: "Chat Assistant",
  TRIP_PLANNER: "Trip Planner",
  DESCRIPTION_GENERATOR: "Description Generator",
  BUDGET_PLANNER: "Budget Planner",
  SMART_SEARCH: "Smart Search",
};

export function AiStats() {
  const query = useQuery({ queryKey: ["admin", "ai-usage"], queryFn: adminApi.aiUsage });

  return (
    <div className="mx-auto max-w-5xl px-4 py-8 sm:px-6">
      <h1 className="text-2xl font-bold text-neutral-900">AI Usage Statistics</h1>

      {query.isLoading && <LoadingSpinner label="Loading AI usage stats…" />}
      {query.isError && <ErrorBanner message={extractErrorMessage(query.error)} />}

      {query.data && (
        <>
          <section className="mt-6 grid grid-cols-3 gap-4">
            <StatTile label="Total Calls" value={query.data.totalCalls} />
            <StatTile label="Successful" value={query.data.successCount} />
            <StatTile label="Failed" value={query.data.failureCount} />
          </section>

          <section className="mt-8">
            <h2 className="mb-3 text-sm font-semibold uppercase tracking-wide text-neutral-500">Calls per feature</h2>
            <BarChart
              data={query.data.perFeature
                .filter((f) => f.totalCalls > 0)
                .map((f) => ({ label: FEATURE_LABELS[f.feature] ?? f.feature, value: f.totalCalls }))}
            />
          </section>

          <section className="mt-8">
            <h2 className="mb-3 text-sm font-semibold uppercase tracking-wide text-neutral-500">Feature detail</h2>
            <div className="overflow-x-auto rounded-2xl border border-neutral-200 bg-white">
              <table className="w-full text-left text-sm">
                <thead className="border-b border-neutral-200 text-xs uppercase text-neutral-500">
                  <tr>
                    <th className="px-4 py-3">Feature</th>
                    <th className="px-4 py-3">Calls</th>
                    <th className="px-4 py-3">Success</th>
                    <th className="px-4 py-3">Failures</th>
                    <th className="px-4 py-3">Avg latency</th>
                  </tr>
                </thead>
                <tbody>
                  {query.data.perFeature.map((f) => (
                    <tr key={f.feature} className="border-b border-neutral-100 last:border-0">
                      <td className="px-4 py-3 font-medium text-neutral-900">{FEATURE_LABELS[f.feature] ?? f.feature}</td>
                      <td className="px-4 py-3">{f.totalCalls}</td>
                      <td className="px-4 py-3 text-green-700">{f.successCount}</td>
                      <td className="px-4 py-3 text-red-600">{f.failureCount}</td>
                      <td className="px-4 py-3">{Math.round(f.avgLatencyMs)} ms</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </section>
        </>
      )}
    </div>
  );
}
