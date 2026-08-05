import { useState } from "react";
import { Link, useParams } from "react-router-dom";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { propertiesApi } from "../../api/properties";
import { LoadingSpinner } from "../../components/LoadingSpinner";
import { ErrorBanner } from "../../components/ErrorBanner";
import { extractErrorMessage } from "../../api/client";

export function PropertyCalendar() {
  const { id } = useParams<{ id: string }>();
  const propertyId = Number(id);
  const queryClient = useQueryClient();

  const [startDate, setStartDate] = useState("");
  const [endDate, setEndDate] = useState("");

  const propertyQuery = useQuery({
    queryKey: ["property", propertyId],
    queryFn: () => propertiesApi.getById(propertyId),
    enabled: !Number.isNaN(propertyId),
  });

  const availabilityQuery = useQuery({
    queryKey: ["availability", propertyId],
    queryFn: () => propertiesApi.availability(propertyId),
    enabled: !Number.isNaN(propertyId),
  });

  const blockMutation = useMutation({
    mutationFn: () => propertiesApi.blockDates(propertyId, startDate, endDate),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["availability", propertyId] });
      setStartDate("");
      setEndDate("");
    },
  });

  const unblockMutation = useMutation({
    mutationFn: (blockId: number) => propertiesApi.unblockDates(propertyId, blockId),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ["availability", propertyId] }),
  });

  const handleBlock = (e: React.FormEvent) => {
    e.preventDefault();
    blockMutation.mutate();
  };

  return (
    <div className="mx-auto max-w-3xl px-4 py-8 sm:px-6">
      <Link to="/host" className="text-sm text-neutral-500 hover:underline">
        ← Back to Host Dashboard
      </Link>

      <h1 className="mt-2 text-2xl font-bold text-neutral-900">
        Availability calendar {propertyQuery.data && `— ${propertyQuery.data.title}`}
      </h1>
      <p className="mt-1 text-sm text-neutral-500">
        Block dates for maintenance or personal use. Dates booked by guests are shown automatically and can't be
        removed here — cancel the booking instead.
      </p>

      <form onSubmit={handleBlock} className="mt-6 flex flex-wrap items-end gap-3 rounded-2xl border border-neutral-200 bg-white p-4">
        <div>
          <label className="mb-1 block text-xs font-semibold text-neutral-500">Start date</label>
          <input
            required
            type="date"
            value={startDate}
            onChange={(e) => setStartDate(e.target.value)}
            className="rounded-lg border border-neutral-300 px-3 py-1.5 text-sm"
          />
        </div>
        <div>
          <label className="mb-1 block text-xs font-semibold text-neutral-500">End date</label>
          <input
            required
            type="date"
            value={endDate}
            onChange={(e) => setEndDate(e.target.value)}
            className="rounded-lg border border-neutral-300 px-3 py-1.5 text-sm"
          />
        </div>
        <button
          type="submit"
          disabled={blockMutation.isPending}
          className="rounded-full bg-neutral-900 px-4 py-2 text-sm font-medium text-white disabled:opacity-50"
        >
          Block dates
        </button>
      </form>
      {blockMutation.isError && (
        <div className="mt-3">
          <ErrorBanner message={extractErrorMessage(blockMutation.error)} />
        </div>
      )}

      <div className="mt-6">
        {availabilityQuery.isLoading && <LoadingSpinner label="Loading calendar…" />}
        <div className="flex flex-col gap-2">
          {availabilityQuery.data?.map((b) => (
            <div key={b.id} className="flex items-center justify-between rounded-xl border border-neutral-200 bg-white p-3 text-sm">
              <div>
                <span className={`mr-2 rounded-full px-2 py-0.5 text-xs font-medium ${b.reason === "BOOKED" ? "bg-amber-50 text-amber-700" : "bg-neutral-100 text-neutral-600"}`}>
                  {b.reason}
                </span>
                {b.startDate} → {b.endDate}
              </div>
              {b.reason === "BLOCKED" && (
                <button
                  onClick={() => unblockMutation.mutate(b.id)}
                  className="text-xs font-medium text-red-500 hover:text-red-700"
                >
                  Remove
                </button>
              )}
            </div>
          ))}
          {availabilityQuery.data && availabilityQuery.data.length === 0 && (
            <p className="text-sm text-neutral-400">No blocked or booked dates.</p>
          )}
        </div>
      </div>
    </div>
  );
}
