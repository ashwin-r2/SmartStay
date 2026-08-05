import { useState } from "react";
import { Link } from "react-router-dom";
import { useQuery } from "@tanstack/react-query";
import { bookingsApi } from "../../api/bookings";
import { LoadingSpinner } from "../../components/LoadingSpinner";
import { ErrorBanner } from "../../components/ErrorBanner";
import { Pagination } from "../../components/Pagination";
import { StatusBadge } from "../../components/StatusBadge";
import { extractErrorMessage } from "../../api/client";

export function MyBookings() {
  const [page, setPage] = useState(0);

  const query = useQuery({
    queryKey: ["bookings", "me", page],
    queryFn: () => bookingsApi.myBookings(page, 10),
  });

  return (
    <div className="mx-auto max-w-4xl px-4 py-8 sm:px-6">
      <h1 className="text-2xl font-bold text-neutral-900">My Bookings</h1>

      {query.isLoading && <LoadingSpinner label="Loading your bookings…" />}
      {query.isError && <ErrorBanner message={extractErrorMessage(query.error)} />}

      {query.data && query.data.content.length === 0 && (
        <p className="mt-6 text-neutral-500">
          You haven't booked anything yet.{" "}
          <Link to="/search" className="text-brand-600 underline">
            Start exploring
          </Link>
          .
        </p>
      )}

      <div className="mt-6 flex flex-col gap-4">
        {query.data?.content.map((b) => (
          <Link
            key={b.id}
            to={`/bookings/${b.id}`}
            className="flex gap-4 rounded-2xl border border-neutral-200 bg-white p-4 hover:shadow-sm"
          >
            <div className="h-20 w-24 shrink-0 overflow-hidden rounded-xl bg-neutral-100">
              {b.propertyCoverImageUrl && (
                <img src={b.propertyCoverImageUrl} alt={b.propertyTitle} className="h-full w-full object-cover" />
              )}
            </div>
            <div className="flex-1">
              <div className="flex items-start justify-between">
                <h2 className="font-semibold text-neutral-900">{b.propertyTitle}</h2>
                <StatusBadge status={b.status} />
              </div>
              <p className="text-sm text-neutral-500">{b.propertyCity}</p>
              <p className="mt-1 text-sm text-neutral-600">
                {b.checkIn} → {b.checkOut} · {b.nights} nights · {b.guestsCount} guests
              </p>
              <p className="mt-1 text-sm font-medium text-neutral-900">₹{b.totalPrice.toLocaleString()}</p>
            </div>
          </Link>
        ))}
      </div>

      {query.data && <Pagination page={query.data.page} totalPages={query.data.totalPages} onPageChange={setPage} />}
    </div>
  );
}
