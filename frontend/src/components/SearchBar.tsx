import { useState, type FormEvent } from "react";
import { useNavigate } from "react-router-dom";
import { Search } from "lucide-react";

interface SearchBarProps {
  initial?: { city?: string; checkIn?: string; checkOut?: string; guests?: number };
}

export function SearchBar({ initial }: SearchBarProps) {
  const navigate = useNavigate();
  const [city, setCity] = useState(initial?.city ?? "");
  const [checkIn, setCheckIn] = useState(initial?.checkIn ?? "");
  const [checkOut, setCheckOut] = useState(initial?.checkOut ?? "");
  const [guests, setGuests] = useState(initial?.guests?.toString() ?? "");

  const handleSubmit = (e: FormEvent) => {
    e.preventDefault();
    const params = new URLSearchParams();
    if (city) params.set("city", city);
    if (checkIn) params.set("checkIn", checkIn);
    if (checkOut) params.set("checkOut", checkOut);
    if (guests) params.set("guests", guests);
    navigate(`/search?${params.toString()}`);
  };

  return (
    <form
      onSubmit={handleSubmit}
      className="flex w-full flex-col gap-3 rounded-2xl border border-neutral-200 bg-white p-3 shadow-sm sm:flex-row sm:items-center sm:gap-0 sm:divide-x sm:divide-neutral-200"
    >
      <div className="flex-1 px-3 py-1">
        <label className="block text-xs font-semibold text-neutral-500">Where</label>
        <input
          value={city}
          onChange={(e) => setCity(e.target.value)}
          placeholder="Search destinations"
          className="w-full border-none p-0 text-sm outline-none placeholder:text-neutral-400"
        />
      </div>
      <div className="flex-1 px-3 py-1">
        <label className="block text-xs font-semibold text-neutral-500">Check in</label>
        <input
          type="date"
          value={checkIn}
          onChange={(e) => setCheckIn(e.target.value)}
          className="w-full border-none p-0 text-sm outline-none"
        />
      </div>
      <div className="flex-1 px-3 py-1">
        <label className="block text-xs font-semibold text-neutral-500">Check out</label>
        <input
          type="date"
          value={checkOut}
          onChange={(e) => setCheckOut(e.target.value)}
          className="w-full border-none p-0 text-sm outline-none"
        />
      </div>
      <div className="flex-1 px-3 py-1">
        <label className="block text-xs font-semibold text-neutral-500">Guests</label>
        <input
          type="number"
          min={1}
          value={guests}
          onChange={(e) => setGuests(e.target.value)}
          placeholder="Add guests"
          className="w-full border-none p-0 text-sm outline-none placeholder:text-neutral-400"
        />
      </div>
      <button
        type="submit"
        className="flex items-center justify-center gap-2 rounded-xl bg-brand-600 px-5 py-3 text-sm font-semibold text-white hover:bg-brand-700 sm:ml-2"
      >
        <Search size={16} /> Search
      </button>
    </form>
  );
}
