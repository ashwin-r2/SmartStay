import { useState } from "react";
import { useParams, Link } from "react-router-dom";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { bookingsApi } from "../../api/bookings";
import { reviewsApi } from "../../api/reviews";
import { LoadingSpinner } from "../../components/LoadingSpinner";
import { ErrorBanner } from "../../components/ErrorBanner";
import { StatusBadge } from "../../components/StatusBadge";
import { StarRating } from "../../components/StarRating";
import { extractErrorMessage } from "../../api/client";

export function BookingDetails() {
  const { id } = useParams<{ id: string }>();
  const bookingId = Number(id);
  const queryClient = useQueryClient();

  const [cancelReason, setCancelReason] = useState("");
  const [showCancelForm, setShowCancelForm] = useState(false);
  const [rating, setRating] = useState(5);
  const [comment, setComment] = useState("");
  const [reviewSubmitted, setReviewSubmitted] = useState(false);

  const bookingQuery = useQuery({
    queryKey: ["booking", bookingId],
    queryFn: () => bookingsApi.getById(bookingId),
    enabled: !Number.isNaN(bookingId),
  });

  const eligibilityQuery = useQuery({
    queryKey: ["review-eligibility", bookingId],
    queryFn: () => reviewsApi.eligibility(bookingId),
    enabled: !Number.isNaN(bookingId) && bookingQuery.data?.status === "COMPLETED",
  });

  const cancelMutation = useMutation({
    mutationFn: () => bookingsApi.cancel(bookingId, cancelReason),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["booking", bookingId] });
      setShowCancelForm(false);
    },
  });

  const reviewMutation = useMutation({
    mutationFn: () => reviewsApi.create({ bookingId, rating, comment }),
    onSuccess: () => {
      setReviewSubmitted(true);
      queryClient.invalidateQueries({ queryKey: ["review-eligibility", bookingId] });
    },
  });

  if (bookingQuery.isLoading) return <LoadingSpinner label="Loading booking…" />;
  if (bookingQuery.isError || !bookingQuery.data) {
    return (
      <div className="mx-auto max-w-2xl px-4 py-16">
        <ErrorBanner message="We couldn't find this booking." />
      </div>
    );
  }

  const b = bookingQuery.data;
  const canCancel = b.status === "CONFIRMED" || b.status === "PENDING";
  const canReview = b.status === "COMPLETED" && eligibilityQuery.data === true && !reviewSubmitted;

  return (
    <div className="mx-auto max-w-2xl px-4 py-8 sm:px-6">
      <Link to="/bookings" className="text-sm text-neutral-500 hover:underline">
        ← Back to My Bookings
      </Link>

      <div className="mt-4 rounded-2xl border border-neutral-200 bg-white p-6">
        <div className="flex items-start justify-between">
          <h1 className="text-xl font-bold text-neutral-900">{b.propertyTitle}</h1>
          <StatusBadge status={b.status} />
        </div>
        <p className="text-sm text-neutral-500">{b.propertyCity}</p>

        <dl className="mt-4 grid grid-cols-2 gap-3 text-sm">
          <div>
            <dt className="text-neutral-500">Check-in</dt>
            <dd className="font-medium text-neutral-900">{b.checkIn}</dd>
          </div>
          <div>
            <dt className="text-neutral-500">Check-out</dt>
            <dd className="font-medium text-neutral-900">{b.checkOut}</dd>
          </div>
          <div>
            <dt className="text-neutral-500">Guests</dt>
            <dd className="font-medium text-neutral-900">{b.guestsCount}</dd>
          </div>
          <div>
            <dt className="text-neutral-500">Nights</dt>
            <dd className="font-medium text-neutral-900">{b.nights}</dd>
          </div>
          <div>
            <dt className="text-neutral-500">Total paid</dt>
            <dd className="font-medium text-neutral-900">₹{b.totalPrice.toLocaleString()}</dd>
          </div>
        </dl>

        {b.status === "CANCELLED" && b.cancellationReason && (
          <p className="mt-4 rounded-lg bg-red-50 p-3 text-sm text-red-700">
            Cancelled: {b.cancellationReason}
          </p>
        )}

        {canCancel && (
          <div className="mt-6 border-t border-neutral-200 pt-4">
            {!showCancelForm ? (
              <button
                onClick={() => setShowCancelForm(true)}
                className="rounded-full border border-red-300 px-4 py-2 text-sm font-medium text-red-600 hover:bg-red-50"
              >
                Cancel booking
              </button>
            ) : (
              <div className="flex flex-col gap-2">
                <textarea
                  value={cancelReason}
                  onChange={(e) => setCancelReason(e.target.value)}
                  placeholder="Reason for cancellation"
                  className="w-full rounded-lg border border-neutral-300 p-2 text-sm"
                  rows={2}
                />
                {cancelMutation.isError && (
                  <p className="text-xs text-red-600">{extractErrorMessage(cancelMutation.error)}</p>
                )}
                <div className="flex gap-2">
                  <button
                    onClick={() => cancelMutation.mutate()}
                    disabled={!cancelReason.trim() || cancelMutation.isPending}
                    className="rounded-full bg-red-600 px-4 py-2 text-sm font-medium text-white disabled:opacity-50"
                  >
                    Confirm cancellation
                  </button>
                  <button
                    onClick={() => setShowCancelForm(false)}
                    className="rounded-full border border-neutral-300 px-4 py-2 text-sm font-medium text-neutral-600"
                  >
                    Keep booking
                  </button>
                </div>
              </div>
            )}
          </div>
        )}

        {b.status === "COMPLETED" && (
          <div className="mt-6 border-t border-neutral-200 pt-4">
            {reviewSubmitted ? (
              <p className="text-sm font-medium text-green-600">Thanks for your review!</p>
            ) : canReview ? (
              <div>
                <h2 className="font-semibold text-neutral-900">Leave a review</h2>
                <div className="mt-2 flex items-center gap-2">
                  {[1, 2, 3, 4, 5].map((n) => (
                    <button key={n} onClick={() => setRating(n)}>
                      <StarRating rating={n <= rating ? n : 0} />
                    </button>
                  ))}
                </div>
                <textarea
                  value={comment}
                  onChange={(e) => setComment(e.target.value)}
                  placeholder="How was your stay?"
                  className="mt-2 w-full rounded-lg border border-neutral-300 p-2 text-sm"
                  rows={3}
                />
                {reviewMutation.isError && (
                  <p className="mt-1 text-xs text-red-600">{extractErrorMessage(reviewMutation.error)}</p>
                )}
                <button
                  onClick={() => reviewMutation.mutate()}
                  disabled={reviewMutation.isPending}
                  className="mt-2 rounded-full bg-brand-600 px-4 py-2 text-sm font-medium text-white disabled:opacity-50"
                >
                  Submit review
                </button>
              </div>
            ) : (
              <p className="text-sm text-neutral-500">You've already reviewed this stay, or it's not eligible for review.</p>
            )}
          </div>
        )}
      </div>
    </div>
  );
}
