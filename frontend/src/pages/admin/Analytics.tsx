import { useQuery } from "@tanstack/react-query";
import { adminApi } from "../../api/admin";
import { LoadingSpinner } from "../../components/LoadingSpinner";
import { ErrorBanner } from "../../components/ErrorBanner";
import { StatTile } from "../../components/StatTile";
import { BarChart } from "../../components/BarChart";
import { extractErrorMessage } from "../../api/client";

export function Analytics() {
  const query = useQuery({ queryKey: ["admin", "booking-analytics"], queryFn: adminApi.bookingAnalytics });

  return (
    <div className="mx-auto max-w-5xl px-4 py-8 sm:px-6">
      <h1 className="text-2xl font-bold text-neutral-900">Booking Analytics</h1>

      {query.isLoading && <LoadingSpinner label="Loading analytics…" />}
      {query.isError && <ErrorBanner message={extractErrorMessage(query.error)} />}

      {query.data && (
        <>
          <section className="mt-6">
            <h2 className="mb-3 text-sm font-semibold uppercase tracking-wide text-neutral-500">Status breakdown</h2>
            <div className="grid grid-cols-2 gap-4 sm:grid-cols-4">
              <StatTile label="Confirmed" value={query.data.confirmed} />
              <StatTile label="Completed" value={query.data.completed} />
              <StatTile label="Cancelled" value={query.data.cancelled} />
              <StatTile label="Pending" value={query.data.pending} />
            </div>
            {/* Status colors are reserved/consistent with StatusBadge: green=confirmed/completed-positive,
                amber=pending, red=cancelled — never reused for unrelated series. */}
            <div className="mt-3 flex gap-2 text-xs">
              <span className="rounded-full bg-green-50 px-2.5 py-1 font-medium text-green-700">
                Confirmed {query.data.totalBookings > 0 ? Math.round((query.data.confirmed / query.data.totalBookings) * 100) : 0}%
              </span>
              <span className="rounded-full bg-neutral-100 px-2.5 py-1 font-medium text-neutral-600">
                Completed {query.data.totalBookings > 0 ? Math.round((query.data.completed / query.data.totalBookings) * 100) : 0}%
              </span>
              <span className="rounded-full bg-red-50 px-2.5 py-1 font-medium text-red-700">
                Cancelled {query.data.totalBookings > 0 ? Math.round((query.data.cancelled / query.data.totalBookings) * 100) : 0}%
              </span>
            </div>
          </section>

          <section className="mt-8">
            <h2 className="mb-3 text-sm font-semibold uppercase tracking-wide text-neutral-500">Revenue</h2>
            <StatTile label="Total Revenue" value={`₹${query.data.totalRevenue.toLocaleString()}`} />
          </section>

          <section className="mt-8">
            <h2 className="mb-3 text-sm font-semibold uppercase tracking-wide text-neutral-500">Monthly bookings</h2>
            {query.data.monthlyStats.length > 0 ? (
              <BarChart
                data={query.data.monthlyStats.map((m) => ({ label: m.month.slice(0, 10), value: m.bookingCount }))}
              />
            ) : (
              <p className="text-sm text-neutral-400">
                Monthly trend data requires PostgreSQL (uses date_trunc) — not available when running against H2.
              </p>
            )}
          </section>
        </>
      )}
    </div>
  );
}
