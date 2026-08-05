import { useState } from "react";
import { Link } from "react-router-dom";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { adminApi } from "../../api/admin";
import { LoadingSpinner } from "../../components/LoadingSpinner";
import { ErrorBanner } from "../../components/ErrorBanner";
import { Pagination } from "../../components/Pagination";
import { StatusBadge } from "../../components/StatusBadge";
import { StarRating } from "../../components/StarRating";
import { extractErrorMessage } from "../../api/client";

export function ManageProperties() {
  const [page, setPage] = useState(0);
  const queryClient = useQueryClient();

  const query = useQuery({
    queryKey: ["admin", "properties", page],
    queryFn: () => adminApi.properties(page, 20),
  });

  const deleteMutation = useMutation({
    mutationFn: (id: number) => adminApi.deleteProperty(id),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ["admin", "properties"] }),
  });

  return (
    <div className="mx-auto max-w-6xl px-4 py-8 sm:px-6">
      <h1 className="text-2xl font-bold text-neutral-900">Manage Properties</h1>

      {query.isLoading && <LoadingSpinner label="Loading properties…" />}
      {query.isError && <ErrorBanner message={extractErrorMessage(query.error)} />}

      {query.data && (
        <div className="mt-4 grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
          {query.data.content.map((p) => (
            <div key={p.id} className="rounded-2xl border border-neutral-200 bg-white p-4">
              <div className="flex items-start justify-between gap-2">
                <Link to={`/properties/${p.id}`} className="font-medium text-neutral-900 hover:text-brand-600">
                  {p.title}
                </Link>
                <StatusBadge status={p.status} />
              </div>
              <p className="text-xs text-neutral-500">
                {p.city}, {p.country}
              </p>
              <div className="mt-1 flex items-center gap-2 text-sm">
                <StarRating rating={p.avgRating} />
                <span className="text-neutral-400">·</span>
                <span>₹{p.pricePerNight.toLocaleString()}/night</span>
              </div>
              <button
                onClick={() => {
                  if (confirm(`Delete "${p.title}"? This cannot be undone.`)) deleteMutation.mutate(p.id);
                }}
                className="mt-3 text-xs font-medium text-red-500 hover:text-red-700"
              >
                Delete listing
              </button>
            </div>
          ))}
        </div>
      )}

      {query.data && <Pagination page={query.data.page} totalPages={query.data.totalPages} onPageChange={setPage} />}
    </div>
  );
}
