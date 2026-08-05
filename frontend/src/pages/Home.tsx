import { useQuery } from "@tanstack/react-query";
import { Sparkles } from "lucide-react";
import { SearchBar } from "../components/SearchBar";
import { SmartSearchBox } from "../components/SmartSearchBox";
import { PropertyCard } from "../components/PropertyCard";
import { LoadingSpinner } from "../components/LoadingSpinner";
import { propertiesApi } from "../api/properties";
import { aiApi } from "../api/ai";
import { useAuth } from "../context/AuthContext";

export function Home() {
  const { user } = useAuth();

  const popularQuery = useQuery({
    queryKey: ["properties", "popular"],
    queryFn: () => propertiesApi.search({ size: 8 }),
  });

  const recommendationsQuery = useQuery({
    queryKey: ["ai", "recommendations"],
    queryFn: () => aiApi.recommendations(),
    enabled: !!user,
    retry: false,
  });

  return (
    <div>
      <section className="bg-gradient-to-b from-brand-50 to-white px-4 py-14 sm:px-6">
        <div className="mx-auto max-w-4xl text-center">
          <h1 className="text-3xl font-bold text-neutral-900 sm:text-4xl">
            Find your next stay, planned smarter with AI
          </h1>
          <p className="mt-3 text-neutral-600">
            Search thousands of stays, or just tell our AI what you're looking for.
          </p>
        </div>
        <div className="mx-auto mt-8 flex max-w-4xl flex-col gap-4">
          <SearchBar />
          <SmartSearchBox />
        </div>
      </section>

      {user && (
        <section className="mx-auto max-w-7xl px-4 py-10 sm:px-6">
          <div className="mb-4 flex items-center gap-2">
            <Sparkles size={20} className="text-brand-600" />
            <h2 className="text-xl font-semibold text-neutral-900">Recommended for you</h2>
          </div>
          {recommendationsQuery.isLoading && <LoadingSpinner label="Asking the AI for recommendations…" />}
          {recommendationsQuery.data && (
            <>
              <p className="mb-4 max-w-3xl text-sm text-neutral-600">{recommendationsQuery.data.summary}</p>
              <div className="grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-4">
                {recommendationsQuery.data.properties.map((p) => (
                  <PropertyCard key={p.id} property={p} />
                ))}
              </div>
            </>
          )}
          {recommendationsQuery.isError && (
            <p className="text-sm text-neutral-500">
              AI recommendations aren't available right now — check out our popular stays below instead.
            </p>
          )}
        </section>
      )}

      <section className="mx-auto max-w-7xl px-4 py-10 sm:px-6">
        <h2 className="mb-4 text-xl font-semibold text-neutral-900">Popular stays</h2>
        {popularQuery.isLoading && <LoadingSpinner label="Loading properties…" />}
        {popularQuery.data && (
          <div className="grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-4">
            {popularQuery.data.content.map((p) => (
              <PropertyCard key={p.id} property={p} />
            ))}
          </div>
        )}
        {popularQuery.data && popularQuery.data.content.length === 0 && (
          <p className="text-sm text-neutral-500">No properties listed yet — check back soon!</p>
        )}
      </section>
    </div>
  );
}
