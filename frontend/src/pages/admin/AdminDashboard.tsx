import { Link } from "react-router-dom";
import { useQuery } from "@tanstack/react-query";
import { Users, Home, CalendarCheck, IndianRupee, Star, Sparkles } from "lucide-react";
import { adminApi } from "../../api/admin";
import { LoadingSpinner } from "../../components/LoadingSpinner";
import { ErrorBanner } from "../../components/ErrorBanner";
import { StatTile } from "../../components/StatTile";
import { extractErrorMessage } from "../../api/client";

export function AdminDashboard() {
  const query = useQuery({ queryKey: ["admin", "dashboard"], queryFn: adminApi.dashboard });

  return (
    <div className="mx-auto max-w-6xl px-4 py-8 sm:px-6">
      <div className="flex flex-wrap items-center justify-between gap-3">
        <h1 className="text-2xl font-bold text-neutral-900">Admin Dashboard</h1>
        <div className="flex gap-2 text-sm">
          <Link to="/admin/users" className="rounded-full border border-neutral-300 px-3 py-1.5 hover:bg-neutral-50">
            Manage Users
          </Link>
          <Link to="/admin/properties" className="rounded-full border border-neutral-300 px-3 py-1.5 hover:bg-neutral-50">
            Manage Properties
          </Link>
          <Link to="/admin/amenities" className="rounded-full border border-neutral-300 px-3 py-1.5 hover:bg-neutral-50">
            Manage Amenities
          </Link>
          <Link to="/admin/analytics" className="rounded-full border border-neutral-300 px-3 py-1.5 hover:bg-neutral-50">
            Booking Analytics
          </Link>
          <Link to="/admin/ai-usage" className="rounded-full border border-neutral-300 px-3 py-1.5 hover:bg-neutral-50">
            AI Usage
          </Link>
        </div>
      </div>

      {query.isLoading && <LoadingSpinner label="Loading dashboard…" />}
      {query.isError && <ErrorBanner message={extractErrorMessage(query.error)} />}

      {query.data && (
        <>
          <section className="mt-6 grid grid-cols-2 gap-4 sm:grid-cols-3 lg:grid-cols-4">
            <StatTile label="Total Users" value={query.data.totalUsers} icon={<Users size={16} />} hint={`${query.data.totalGuests} guests · ${query.data.totalHosts} hosts · ${query.data.totalAdmins} admins`} />
            <StatTile label="Properties" value={query.data.totalProperties} icon={<Home size={16} />} hint={`${query.data.activeProperties} active · ${query.data.inactiveProperties} inactive`} />
            <StatTile label="Bookings" value={query.data.totalBookings} icon={<CalendarCheck size={16} />} hint={`${query.data.confirmedBookings} confirmed · ${query.data.completedBookings} completed · ${query.data.cancelledBookings} cancelled`} />
            <StatTile label="Total Revenue" value={`₹${query.data.totalRevenue.toLocaleString()}`} icon={<IndianRupee size={16} />} />
            <StatTile label="Reviews" value={query.data.totalReviews} icon={<Star size={16} />} />
            <Link to="/admin/ai-usage" className="block">
              <StatTile label="AI Usage" value="View stats" icon={<Sparkles size={16} />} hint="Calls per feature, success rate, latency" />
            </Link>
          </section>
        </>
      )}
    </div>
  );
}
