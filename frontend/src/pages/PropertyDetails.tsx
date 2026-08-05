import { useMemo, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { MapPin, Sparkles, Users } from "lucide-react";
import { propertiesApi } from "../api/properties";
import { reviewsApi } from "../api/reviews";
import { bookingsApi } from "../api/bookings";
import { aiApi } from "../api/ai";
import { useAuth } from "../context/AuthContext";
import { LoadingSpinner } from "../components/LoadingSpinner";
import { ErrorBanner } from "../components/ErrorBanner";
import { StarRating } from "../components/StarRating";
import { extractErrorMessage } from "../api/client";

export function PropertyDetails() {
  const { id } = useParams<{ id: string }>();
  const propertyId = Number(id);
  const { user } = useAuth();
  const navigate = useNavigate();
  const queryClient = useQueryClient();

  const [checkIn, setCheckIn] = useState("");
  const [checkOut, setCheckOut] = useState("");
  const [guestsCount, setGuestsCount] = useState(1);
  const [bookingError, setBookingError] = useState<string | null>(null);
  const [bookingSuccess, setBookingSuccess] = useState(false);
  const [aiSummary, setAiSummary] = useState<string | null>(null);
  const [aiSummaryError, setAiSummaryError] = useState<string | null>(null);
  const [aiSummaryLoading, setAiSummaryLoading] = useState(false);

  const propertyQuery = useQuery({
    queryKey: ["property", propertyId],
    queryFn: () => propertiesApi.getById(propertyId),
    enabled: !Number.isNaN(propertyId),
  });

  const reviewsQuery = useQuery({
    queryKey: ["reviews", propertyId],
    queryFn: () => reviewsApi.byProperty(propertyId, 0, 10),
    enabled: !Number.isNaN(propertyId),
  });

  const nights = useMemo(() => {
    if (!checkIn || !checkOut) return 0;
    const ms = new Date(checkOut).getTime() - new Date(checkIn).getTime();
    return Math.max(0, Math.round(ms / (1000 * 60 * 60 * 24)));
  }, [checkIn, checkOut]);

  const total = useMemo(() => {
    if (!propertyQuery.data || nights <= 0) return 0;
    return propertyQuery.data.pricePerNight * nights + propertyQuery.data.cleaningFee;
  }, [propertyQuery.data, nights]);

  const bookMutation = useMutation({
    mutationFn: () => bookingsApi.create({ propertyId, checkIn, checkOut, guestsCount }),
    onSuccess: () => {
      setBookingSuccess(true);
      setBookingError(null);
      queryClient.invalidateQueries({ queryKey: ["property", propertyId] });
    },
    onError: (err) => setBookingError(extractErrorMessage(err, "Could not complete the booking.")),
  });

  const handleBook = () => {
    setBookingError(null);
    if (!user) {
      navigate("/login", { state: { from: { pathname: `/properties/${propertyId}` } } });
      return;
    }
    if (!checkIn || !checkOut) {
      setBookingError("Please select check-in and check-out dates.");
      return;
    }
    bookMutation.mutate();
  };

  const handleAiSummary = async () => {
    setAiSummaryLoading(true);
    setAiSummaryError(null);
    try {
      const res = await aiApi.reviewSummary(propertyId);
      setAiSummary(res.summary);
    } catch (err) {
      setAiSummaryError(extractErrorMessage(err, "AI summary is unavailable right now."));
    } finally {
      setAiSummaryLoading(false);
    }
  };

  if (propertyQuery.isLoading) return <LoadingSpinner label="Loading property…" />;
  if (propertyQuery.isError || !propertyQuery.data) {
    return (
      <div className="mx-auto max-w-3xl px-4 py-16">
        <ErrorBanner message="We couldn't find this property." />
      </div>
    );
  }

  const p = propertyQuery.data;

  return (
    <div className="mx-auto max-w-6xl px-4 py-8 sm:px-6">
      <h1 className="text-2xl font-bold text-neutral-900">{p.title}</h1>
      <div className="mt-1 flex items-center gap-3 text-sm text-neutral-500">
        <StarRating rating={p.avgRating} />
        <span>·</span>
        <span className="flex items-center gap-1">
          <MapPin size={14} /> {p.city}, {p.country}
        </span>
      </div>

      <div className="mt-4 grid grid-cols-2 gap-2 overflow-hidden rounded-2xl sm:grid-cols-4">
        {p.images.length > 0 ? (
          p.images.slice(0, 4).map((img, i) => (
            <img
              key={img.id}
              src={img.url}
              alt={p.title}
              className={`h-48 w-full object-cover ${i === 0 ? "col-span-2 row-span-2 h-full sm:col-span-2" : ""}`}
            />
          ))
        ) : (
          <div className="col-span-2 flex h-48 w-full items-center justify-center bg-neutral-100 text-neutral-400 sm:col-span-4">
            No images uploaded yet
          </div>
        )}
      </div>

      <div className="mt-8 grid grid-cols-1 gap-10 lg:grid-cols-[1fr_360px]">
        <div>
          <section>
            <h2 className="text-lg font-semibold text-neutral-900">
              {p.roomType.replace("_", " ")} hosted by {p.host.fullName}
            </h2>
            <p className="mt-1 text-sm text-neutral-500">
              {p.maxGuests} guests · {p.bedrooms} bedrooms · {p.beds} beds · {p.bathrooms} bathrooms
            </p>
            {p.aiGeneratedDescription && (
              <span className="mt-2 inline-flex items-center gap-1 rounded-full bg-brand-50 px-2.5 py-1 text-xs font-medium text-brand-700">
                <Sparkles size={12} /> AI-assisted description
              </span>
            )}
            <p className="mt-4 whitespace-pre-line text-sm leading-relaxed text-neutral-700">{p.description}</p>
          </section>

          <section className="mt-8 border-t border-neutral-200 pt-6">
            <h2 className="text-lg font-semibold text-neutral-900">Amenities</h2>
            <div className="mt-3 grid grid-cols-2 gap-2 sm:grid-cols-3">
              {p.amenities.map((a) => (
                <div key={a.id} className="rounded-lg bg-neutral-50 px-3 py-2 text-sm text-neutral-700">
                  {a.name}
                </div>
              ))}
              {p.amenities.length === 0 && <p className="text-sm text-neutral-400">No amenities listed.</p>}
            </div>
          </section>

          <section className="mt-8 border-t border-neutral-200 pt-6">
            <div className="flex items-center justify-between">
              <h2 className="text-lg font-semibold text-neutral-900">
                Reviews ({reviewsQuery.data?.totalElements ?? 0})
              </h2>
              {(reviewsQuery.data?.totalElements ?? 0) > 0 && (
                <button
                  onClick={handleAiSummary}
                  disabled={aiSummaryLoading}
                  className="flex items-center gap-1.5 rounded-full border border-brand-200 bg-brand-50 px-3 py-1.5 text-xs font-medium text-brand-700 hover:bg-brand-100 disabled:opacity-50"
                >
                  <Sparkles size={14} /> {aiSummaryLoading ? "Summarizing…" : "AI Summary"}
                </button>
              )}
            </div>

            {aiSummary && (
              <div className="mt-3 whitespace-pre-line rounded-xl border border-brand-100 bg-brand-50/60 p-4 text-sm text-neutral-700">
                {aiSummary}
              </div>
            )}
            {aiSummaryError && <p className="mt-2 text-xs text-red-600">{aiSummaryError}</p>}

            <div className="mt-4 space-y-4">
              {reviewsQuery.data?.content.map((r) => (
                <div key={r.id} className="border-b border-neutral-100 pb-4">
                  <div className="flex items-center justify-between">
                    <span className="text-sm font-medium text-neutral-800">{r.userName}</span>
                    <StarRating rating={r.rating} />
                  </div>
                  {r.comment && <p className="mt-1 text-sm text-neutral-600">{r.comment}</p>}
                  {r.imageUrls.length > 0 && (
                    <div className="mt-2 flex gap-2">
                      {r.imageUrls.map((url) => (
                        <img key={url} src={url} alt="review" className="h-16 w-16 rounded-lg object-cover" />
                      ))}
                    </div>
                  )}
                </div>
              ))}
              {reviewsQuery.data && reviewsQuery.data.content.length === 0 && (
                <p className="text-sm text-neutral-400">No reviews yet.</p>
              )}
            </div>
          </section>
        </div>

        <aside className="h-fit rounded-2xl border border-neutral-200 p-5 shadow-sm lg:sticky lg:top-24">
          <div className="flex items-baseline gap-1">
            <span className="text-xl font-semibold text-neutral-900">₹{p.pricePerNight.toLocaleString()}</span>
            <span className="text-sm text-neutral-500">/ night</span>
          </div>

          <div className="mt-4 grid grid-cols-2 gap-2">
            <div>
              <label className="mb-1 block text-xs font-semibold text-neutral-500">Check in</label>
              <input
                type="date"
                value={checkIn}
                onChange={(e) => setCheckIn(e.target.value)}
                className="w-full rounded-lg border border-neutral-300 px-2 py-1.5 text-sm"
              />
            </div>
            <div>
              <label className="mb-1 block text-xs font-semibold text-neutral-500">Check out</label>
              <input
                type="date"
                value={checkOut}
                onChange={(e) => setCheckOut(e.target.value)}
                className="w-full rounded-lg border border-neutral-300 px-2 py-1.5 text-sm"
              />
            </div>
          </div>

          <div className="mt-2">
            <label className="mb-1 flex items-center gap-1 text-xs font-semibold text-neutral-500">
              <Users size={12} /> Guests
            </label>
            <input
              type="number"
              min={1}
              max={p.maxGuests}
              value={guestsCount}
              onChange={(e) => setGuestsCount(Number(e.target.value))}
              className="w-full rounded-lg border border-neutral-300 px-2 py-1.5 text-sm"
            />
          </div>

          {nights > 0 && (
            <div className="mt-4 space-y-1 border-t border-neutral-200 pt-4 text-sm text-neutral-600">
              <div className="flex justify-between">
                <span>
                  ₹{p.pricePerNight.toLocaleString()} × {nights} nights
                </span>
                <span>₹{(p.pricePerNight * nights).toLocaleString()}</span>
              </div>
              <div className="flex justify-between">
                <span>Cleaning fee</span>
                <span>₹{p.cleaningFee.toLocaleString()}</span>
              </div>
              <div className="flex justify-between border-t border-neutral-200 pt-1 font-semibold text-neutral-900">
                <span>Total</span>
                <span>₹{total.toLocaleString()}</span>
              </div>
            </div>
          )}

          {bookingError && <p className="mt-3 text-xs text-red-600">{bookingError}</p>}
          {bookingSuccess && (
            <p className="mt-3 text-xs font-medium text-green-600">
              Booking confirmed! View it under{" "}
              <button className="underline" onClick={() => navigate("/bookings")}>
                My Bookings
              </button>
              .
            </p>
          )}

          <button
            onClick={handleBook}
            disabled={bookMutation.isPending}
            className="mt-4 w-full rounded-full bg-brand-600 py-2.5 text-sm font-semibold text-white hover:bg-brand-700 disabled:opacity-50"
          >
            {bookMutation.isPending ? "Booking…" : "Book now"}
          </button>
        </aside>
      </div>
    </div>
  );
}
