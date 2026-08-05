import { useState } from "react";
import { Link } from "react-router-dom";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { Plus, CalendarDays, Pencil, Trash2 } from "lucide-react";
import { useAuth } from "../../context/AuthContext";
import { propertiesApi } from "../../api/properties";
import { bookingsApi } from "../../api/bookings";
import { LoadingSpinner } from "../../components/LoadingSpinner";
import { StatusBadge } from "../../components/StatusBadge";
import { StarRating } from "../../components/StarRating";

export function HostDashboard() {
  const { user } = useAuth();
  const queryClient = useQueryClient();
  const [page] = useState(0);

  const propertiesQuery = useQuery({
    queryKey: ["properties", "host", user?.id],
    queryFn: () => propertiesApi.byHost(user!.id, page, 20),
    enabled: !!user,
  });

  const bookingsQuery = useQuery({
    queryKey: ["bookings", "host", "me"],
    queryFn: () => bookingsApi.myHostBookings(0, 5),
  });

  const deleteMutation = useMutation({
    mutationFn: (id: number) => propertiesApi.remove(id),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ["properties", "host", user?.id] }),
  });

  const toggleStatusMutation = useMutation({
    mutationFn: ({ id, status }: { id: number; status: "ACTIVE" | "INACTIVE" }) =>
      propertiesApi.setStatus(id, status),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ["properties", "host", user?.id] }),
  });

  return (
    <div className="mx-auto max-w-6xl px-4 py-8 sm:px-6">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold text-neutral-900">Host Dashboard</h1>
        <Link
          to="/host/properties/new"
          className="flex items-center gap-1.5 rounded-full bg-brand-600 px-4 py-2 text-sm font-semibold text-white hover:bg-brand-700"
        >
          <Plus size={16} /> Add property
        </Link>
      </div>

      <section className="mt-8">
        <h2 className="mb-3 text-lg font-semibold text-neutral-900">Your listings</h2>
        {propertiesQuery.isLoading && <LoadingSpinner label="Loading your properties…" />}
        {propertiesQuery.data && propertiesQuery.data.content.length === 0 && (
          <p className="text-sm text-neutral-500">You haven't listed any properties yet.</p>
        )}
        <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
          {propertiesQuery.data?.content.map((p) => (
            <div key={p.id} className="flex gap-3 rounded-2xl border border-neutral-200 bg-white p-4">
              <div className="h-20 w-24 shrink-0 overflow-hidden rounded-xl bg-neutral-100">
                {p.coverImageUrl && <img src={p.coverImageUrl} alt={p.title} className="h-full w-full object-cover" />}
              </div>
              <div className="flex-1">
                <div className="flex items-start justify-between gap-2">
                  <h3 className="font-medium text-neutral-900">{p.title}</h3>
                  <StatusBadge status={p.status} />
                </div>
                <p className="text-xs text-neutral-500">{p.city}</p>
                <div className="mt-1 flex items-center gap-2 text-sm">
                  <StarRating rating={p.avgRating} />
                  <span className="text-neutral-400">·</span>
                  <span>₹{p.pricePerNight.toLocaleString()}/night</span>
                </div>
                <div className="mt-2 flex flex-wrap items-center gap-3 text-xs">
                  <Link to={`/host/properties/${p.id}/edit`} className="flex items-center gap-1 text-neutral-600 hover:text-brand-600">
                    <Pencil size={13} /> Edit
                  </Link>
                  <Link to={`/host/properties/${p.id}/calendar`} className="flex items-center gap-1 text-neutral-600 hover:text-brand-600">
                    <CalendarDays size={13} /> Calendar
                  </Link>
                  <button
                    onClick={() =>
                      toggleStatusMutation.mutate({ id: p.id, status: p.status === "ACTIVE" ? "INACTIVE" : "ACTIVE" })
                    }
                    className="text-neutral-600 hover:text-brand-600"
                  >
                    {p.status === "ACTIVE" ? "Deactivate" : "Activate"}
                  </button>
                  <button
                    onClick={() => {
                      if (confirm(`Delete "${p.title}"? This cannot be undone.`)) deleteMutation.mutate(p.id);
                    }}
                    className="flex items-center gap-1 text-red-500 hover:text-red-700"
                  >
                    <Trash2 size={13} /> Delete
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>
      </section>

      <section className="mt-10">
        <h2 className="mb-3 text-lg font-semibold text-neutral-900">Recent bookings</h2>
        {bookingsQuery.data && bookingsQuery.data.content.length === 0 && (
          <p className="text-sm text-neutral-500">No bookings on your properties yet.</p>
        )}
        <div className="flex flex-col gap-2">
          {bookingsQuery.data?.content.map((b) => (
            <div key={b.id} className="flex items-center justify-between rounded-xl border border-neutral-200 bg-white p-3 text-sm">
              <div>
                <span className="font-medium text-neutral-900">{b.propertyTitle}</span>
                <span className="ml-2 text-neutral-500">
                  {b.checkIn} → {b.checkOut} · {b.guestName}
                </span>
              </div>
              <StatusBadge status={b.status} />
            </div>
          ))}
        </div>
      </section>
    </div>
  );
}
