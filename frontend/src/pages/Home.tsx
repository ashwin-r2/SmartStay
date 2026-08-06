import { useQuery } from "@tanstack/react-query";
import { Sparkles } from "lucide-react";
import { SearchBar } from "../components/SearchBar";
import { SmartSearchBox } from "../components/SmartSearchBox";
import { PropertyCard } from "../components/PropertyCard";
import { LoadingSpinner } from "../components/LoadingSpinner";
import { propertiesApi } from "../api/properties";
import { aiApi } from "../api/ai";
import { useAuth } from "../context/AuthContext";

/** Splits the AI-generated recommendation summary into individual list items. */
function summaryToListItems(summary: string): string[] {
  const lines = summary
    .split(/\r?\n/)
    .map((line) => line.trim().replace(/^[-*•]\s*|^\d+[.)]\s*/, ""))
    .filter(Boolean);

  if (lines.length > 1) return lines;

  // Fallback: no line breaks from the AI — split into sentences instead.
  const sentences = summary
    .split(/(?<=[.!?])\s+/)
    .map((s) => s.trim())
    .filter(Boolean);

  return sentences.length > 0 ? sentences : [summary];
}

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
        <section className="px-4 py-10 sm:px-6">
          <div className="mx-auto max-w-7xl rounded-3xl border border-brand-100 bg-gradient-to-br from-brand-50 via-white to-white p-6 sm:p-8">
            <div className="mb-5 flex items-center gap-3">
              <span className="flex h-10 w-10 shrink-0 items-center justify-center rounded-full bg-brand-100 text-brand-600">
                <Sparkles size={20} />
              </span>
              <div>
                <h2 className="text-xl font-semibold text-neutral-900">Recommended for you</h2>
                <p className="text-xs text-neutral-500">Personalized picks powered by AI</p>
              </div>
            </div>

            {recommendationsQuery.isLoading && <LoadingSpinner label="Asking the AI for recommendations…" />}

            {recommendationsQuery.data && (
              <>
                {recommendationsQuery.data.summary && (
                  <div className="mb-6 flex items-start gap-3 rounded-2xl border border-brand-200 bg-white/80 p-4">
                    <Sparkles size={16} className="mt-0.5 shrink-0 text-brand-500" />
                    <ul className="list-disc space-y-1 pl-4 text-sm text-neutral-700">
                      {summaryToListItems(recommendationsQuery.data.summary).map((item, i) => (
                        <li key={i}>{item}</li>
                      ))}
                    </ul>
                  </div>
                )}

                {recommendationsQuery.data.properties.length > 0 ? (
                  <div className="grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-4">
                    {recommendationsQuery.data.properties.map((p) => (
                      <div key={p.id} className="relative">
                        <span className="pointer-events-none absolute left-3 top-3 z-10 flex items-center gap-1 rounded-full bg-neutral-900/80 px-2.5 py-1 text-[11px] font-medium text-white backdrop-blur-sm">
                          <Sparkles size={11} />
                          AI Pick
                        </span>
                        <PropertyCard property={p} />
                      </div>
                    ))}
                  </div>
                ) : (
                  <p className="text-sm text-neutral-500">
                    Book a stay or two and we'll start tailoring recommendations to your taste.
                  </p>
                )}
              </>
            )}

            {recommendationsQuery.isError && (
              <p className="text-sm text-neutral-500">
                AI recommendations aren't available right now — check out our popular stays below instead.
              </p>
            )}
          </div>
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
