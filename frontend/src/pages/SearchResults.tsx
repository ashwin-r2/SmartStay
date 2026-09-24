import { useSearchParams } from "react-router-dom";
import { useQuery } from "@tanstack/react-query";
import { propertiesApi, amenitiesApi } from "../api/properties";
import { PropertyCard } from "../components/PropertyCard";
import { Pagination } from "../components/Pagination";
import { LoadingSpinner } from "../components/LoadingSpinner";
import { ErrorBanner } from "../components/ErrorBanner";
import { SmartSearchBox } from "../components/SmartSearchBox";
import { extractErrorMessage } from "../api/client";

const PROPERTY_TYPES = ["APARTMENT", "HOUSE", "VILLA", "CABIN", "CONDO", "STUDIO", "COTTAGE", "FARM_STAY"];
const ROOM_TYPES = ["ENTIRE_PLACE", "PRIVATE_ROOM", "SHARED_ROOM"];

export function SearchResults() {
  const [searchParams, setSearchParams] = useSearchParams();
  // Page lives in the URL alongside the filters, so any new search (Ask AI, Clear filters,
  // a filter change) that replaces the params also drops back to the first page.
  const page = Math.max(0, Number(searchParams.get("page")) || 0);
  const setPage = (next: number) => {
    const params = new URLSearchParams(searchParams);
    if (next > 0) params.set("page", String(next));
    else params.delete("page");
    setSearchParams(params);
  };

  const city = searchParams.get("city") ?? "";
  const checkIn = searchParams.get("checkIn") ?? "";
  const checkOut = searchParams.get("checkOut") ?? "";
  const guests = searchParams.get("guests") ?? "";
  const minPrice = searchParams.get("minPrice") ?? "";
  const maxPrice = searchParams.get("maxPrice") ?? "";
  const propertyType = searchParams.get("propertyType") ?? "";
  const roomType = searchParams.get("roomType") ?? "";
  const keyword = searchParams.get("keyword") ?? "";
  const amenityIds = (searchParams.get("amenityIds") ?? "")
    .split(",")
    .map((id) => Number(id))
    .filter((id) => Number.isFinite(id) && id > 0);

  const amenitiesQuery = useQuery({ queryKey: ["amenities"], queryFn: amenitiesApi.list });

  const query = useQuery({
    queryKey: ["properties", "search", Object.fromEntries(searchParams), page],
    queryFn: () =>
      propertiesApi.search({
        city: city || undefined,
        checkIn: checkIn || undefined,
        checkOut: checkOut || undefined,
        guests: guests ? Number(guests) : undefined,
        minPrice: minPrice ? Number(minPrice) : undefined,
        maxPrice: maxPrice ? Number(maxPrice) : undefined,
        propertyType: propertyType || undefined,
        roomType: roomType || undefined,
        amenityIds: amenityIds.length > 0 ? amenityIds : undefined,
        keyword: keyword || undefined,
        page,
        size: 12,
      }),
  });

  const updateFilter = (key: string, value: string) => {
    const next = new URLSearchParams(searchParams);
    if (value) next.set(key, value);
    else next.delete(key);
    next.delete("page");
    setSearchParams(next);
  };

  const toggleAmenity = (id: number) => {
    const next = amenityIds.includes(id) ? amenityIds.filter((a) => a !== id) : [...amenityIds, id];
    updateFilter("amenityIds", next.join(","));
  };

  return (
    <div className="mx-auto max-w-7xl px-4 py-8 sm:px-6">
      <div className="mb-6">
        <SmartSearchBox />
      </div>

      <div className="grid grid-cols-1 gap-8 lg:grid-cols-[260px_1fr]">
        <aside className="space-y-5 rounded-2xl border border-neutral-200 bg-white p-5 h-fit">
          <h2 className="font-semibold text-neutral-900">Filters</h2>
          <div>
            <label className="mb-1 block text-xs font-semibold text-neutral-500">City</label>
            <input
              defaultValue={city}
              onBlur={(e) => updateFilter("city", e.target.value)}
              placeholder="e.g. Goa"
              className="w-full rounded-lg border border-neutral-300 px-3 py-1.5 text-sm"
            />
          </div>
          <div className="grid grid-cols-2 gap-2">
            <div>
              <label className="mb-1 block text-xs font-semibold text-neutral-500">Min price</label>
              <input
                type="number"
                defaultValue={minPrice}
                onBlur={(e) => updateFilter("minPrice", e.target.value)}
                className="w-full rounded-lg border border-neutral-300 px-3 py-1.5 text-sm"
              />
            </div>
            <div>
              <label className="mb-1 block text-xs font-semibold text-neutral-500">Max price</label>
              <input
                type="number"
                defaultValue={maxPrice}
                onBlur={(e) => updateFilter("maxPrice", e.target.value)}
                className="w-full rounded-lg border border-neutral-300 px-3 py-1.5 text-sm"
              />
            </div>
          </div>
          <div>
            <label className="mb-1 block text-xs font-semibold text-neutral-500">Guests</label>
            <input
              type="number"
              min={1}
              defaultValue={guests}
              onBlur={(e) => updateFilter("guests", e.target.value)}
              className="w-full rounded-lg border border-neutral-300 px-3 py-1.5 text-sm"
            />
          </div>
          <div>
            <label className="mb-1 block text-xs font-semibold text-neutral-500">Property type</label>
            <select
              value={propertyType}
              onChange={(e) => updateFilter("propertyType", e.target.value)}
              className="w-full rounded-lg border border-neutral-300 px-3 py-1.5 text-sm"
            >
              <option value="">Any</option>
              {PROPERTY_TYPES.map((t) => (
                <option key={t} value={t}>
                  {t.replace("_", " ")}
                </option>
              ))}
            </select>
          </div>
          <div>
            <label className="mb-1 block text-xs font-semibold text-neutral-500">Room type</label>
            <select
              value={roomType}
              onChange={(e) => updateFilter("roomType", e.target.value)}
              className="w-full rounded-lg border border-neutral-300 px-3 py-1.5 text-sm"
            >
              <option value="">Any</option>
              {ROOM_TYPES.map((t) => (
                <option key={t} value={t}>
                  {t.replace("_", " ")}
                </option>
              ))}
            </select>
          </div>
          {amenitiesQuery.data && amenitiesQuery.data.length > 0 && (
            <div>
              <label className="mb-1 block text-xs font-semibold text-neutral-500">Amenities</label>
              <div className="max-h-48 space-y-1.5 overflow-y-auto">
                {amenitiesQuery.data.map((a) => (
                  <label key={a.id} className="flex items-center gap-2 text-sm text-neutral-700">
                    <input
                      type="checkbox"
                      checked={amenityIds.includes(a.id)}
                      onChange={() => toggleAmenity(a.id)}
                      className="rounded border-neutral-300"
                    />
                    {a.name}
                  </label>
                ))}
              </div>
            </div>
          )}
          <button
            onClick={() => setSearchParams(new URLSearchParams())}
            className="w-full rounded-lg border border-neutral-300 py-1.5 text-sm text-neutral-600 hover:bg-neutral-50"
          >
            Clear filters
          </button>
        </aside>

        <div>
          {query.isLoading && <LoadingSpinner label="Searching properties…" />}
          {query.isError && <ErrorBanner message={extractErrorMessage(query.error)} />}
          {query.data && (
            <>
              <p className="mb-4 text-sm text-neutral-500">
                {query.data.totalElements} {query.data.totalElements === 1 ? "stay" : "stays"} found
              </p>
              {query.data.content.length === 0 ? (
                <p className="py-16 text-center text-neutral-500">
                  No properties match your filters. Try widening your search.
                </p>
              ) : (
                <div className="grid grid-cols-1 gap-5 sm:grid-cols-2 xl:grid-cols-3">
                  {query.data.content.map((p) => (
                    <PropertyCard key={p.id} property={p} />
                  ))}
                </div>
              )}
              <Pagination page={query.data.page} totalPages={query.data.totalPages} onPageChange={setPage} />
            </>
          )}
        </div>
      </div>
    </div>
  );
}
