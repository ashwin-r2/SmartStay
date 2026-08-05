import { Link } from "react-router-dom";
import type { PropertySummary } from "../types";
import { StarRating } from "./StarRating";

export function PropertyCard({ property }: { property: PropertySummary }) {
  return (
    <Link
      to={`/properties/${property.id}`}
      className="group flex flex-col overflow-hidden rounded-2xl border border-neutral-200 bg-white transition hover:shadow-lg"
    >
      <div className="aspect-[4/3] w-full overflow-hidden bg-neutral-100">
        {property.coverImageUrl ? (
          <img
            src={property.coverImageUrl}
            alt={property.title}
            className="h-full w-full object-cover transition duration-300 group-hover:scale-105"
            loading="lazy"
          />
        ) : (
          <div className="flex h-full w-full items-center justify-center text-neutral-400">No image yet</div>
        )}
      </div>
      <div className="flex flex-1 flex-col gap-1 p-4">
        <div className="flex items-start justify-between gap-2">
          <h3 className="line-clamp-2 font-semibold text-neutral-900">{property.title}</h3>
          <StarRating rating={property.avgRating} />
        </div>
        <p className="text-sm text-neutral-500">
          {property.city}, {property.country}
        </p>
        <p className="text-xs text-neutral-400">
          {property.propertyType.replace("_", " ")} · up to {property.maxGuests} guests
        </p>
        <div className="mt-auto pt-2">
          <span className="font-semibold text-neutral-900">₹{property.pricePerNight.toLocaleString()}</span>
          <span className="text-sm text-neutral-500"> / night</span>
        </div>
      </div>
    </Link>
  );
}
