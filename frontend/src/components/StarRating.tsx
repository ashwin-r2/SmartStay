import { Star } from "lucide-react";

export function StarRating({ rating, size = 16 }: { rating: number; size?: number }) {
  return (
    <span className="inline-flex items-center gap-1 text-amber-500">
      <Star size={size} fill="currentColor" strokeWidth={0} />
      <span className="text-sm font-medium text-neutral-800">{rating > 0 ? rating.toFixed(1) : "New"}</span>
    </span>
  );
}
