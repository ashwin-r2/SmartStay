import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { Sparkles, Upload, X } from "lucide-react";
import { propertiesApi, amenitiesApi } from "../../api/properties";
import { aiApi } from "../../api/ai";
import { extractErrorMessage } from "../../api/client";
import { ErrorBanner } from "../../components/ErrorBanner";
import { LoadingSpinner } from "../../components/LoadingSpinner";
import type { PropertyRequest, PropertyType, RoomType } from "../../types";

const PROPERTY_TYPES: PropertyType[] = ["APARTMENT", "HOUSE", "VILLA", "CABIN", "CONDO", "STUDIO", "COTTAGE", "FARM_STAY"];
const ROOM_TYPES: RoomType[] = ["ENTIRE_PLACE", "PRIVATE_ROOM", "SHARED_ROOM"];

const emptyForm: PropertyRequest = {
  title: "",
  description: "",
  propertyType: "APARTMENT",
  roomType: "ENTIRE_PLACE",
  addressLine: "",
  city: "",
  state: "",
  country: "",
  zipCode: "",
  pricePerNight: 0,
  cleaningFee: 0,
  maxGuests: 2,
  bedrooms: 1,
  beds: 1,
  bathrooms: 1,
  amenityIds: [],
};

export function PropertyForm() {
  const { id } = useParams<{ id: string }>();
  const isEdit = !!id;
  const propertyId = Number(id);
  const navigate = useNavigate();
  const queryClient = useQueryClient();

  const [form, setForm] = useState<PropertyRequest>(emptyForm);
  const [aiLoading, setAiLoading] = useState(false);
  const [aiError, setAiError] = useState<string | null>(null);
  const [newImages, setNewImages] = useState<File[]>([]);

  const amenitiesQuery = useQuery({ queryKey: ["amenities"], queryFn: amenitiesApi.list });

  const propertyQuery = useQuery({
    queryKey: ["property", propertyId],
    queryFn: () => propertiesApi.getById(propertyId),
    enabled: isEdit && !Number.isNaN(propertyId),
  });

  useEffect(() => {
    if (propertyQuery.data) {
      const p = propertyQuery.data;
      setForm({
        title: p.title,
        description: p.description ?? "",
        propertyType: p.propertyType,
        roomType: p.roomType,
        addressLine: p.addressLine ?? "",
        city: p.city,
        state: p.state ?? "",
        country: p.country,
        zipCode: p.zipCode ?? "",
        pricePerNight: p.pricePerNight,
        cleaningFee: p.cleaningFee,
        maxGuests: p.maxGuests,
        bedrooms: p.bedrooms,
        beds: p.beds,
        bathrooms: p.bathrooms,
        amenityIds: p.amenities.map((a) => a.id),
      });
    }
  }, [propertyQuery.data]);

  const saveMutation = useMutation({
    mutationFn: () => (isEdit ? propertiesApi.update(propertyId, form) : propertiesApi.create(form)),
    onSuccess: async (saved) => {
      if (newImages.length > 0) {
        await propertiesApi.uploadImages(saved.id, newImages);
      }
      queryClient.invalidateQueries({ queryKey: ["properties"] });
      navigate("/host");
    },
  });

  const update = <K extends keyof PropertyRequest>(key: K, value: PropertyRequest[K]) =>
    setForm((f) => ({ ...f, [key]: value }));

  const toggleAmenity = (amenityId: number) => {
    const current = form.amenityIds ?? [];
    update("amenityIds", current.includes(amenityId) ? current.filter((a) => a !== amenityId) : [...current, amenityId]);
  };

  const handleGenerateDescription = async () => {
    setAiError(null);
    setAiLoading(true);
    try {
      const amenityNames = (amenitiesQuery.data ?? [])
        .filter((a) => (form.amenityIds ?? []).includes(a.id))
        .map((a) => a.name);
      const res = await aiApi.descriptionGenerator({
        title: form.title || "Untitled property",
        propertyType: form.propertyType,
        roomType: form.roomType,
        city: form.city || "Unknown city",
        country: form.country || "Unknown country",
        bedrooms: form.bedrooms ?? 1,
        beds: form.beds ?? 1,
        bathrooms: form.bathrooms,
        maxGuests: form.maxGuests,
        amenities: amenityNames,
        pricePerNight: form.pricePerNight,
      });
      update("description", res.description);
    } catch (err) {
      setAiError(extractErrorMessage(err, "AI description generator is unavailable right now."));
    } finally {
      setAiLoading(false);
    }
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    saveMutation.mutate();
  };

  if (isEdit && propertyQuery.isLoading) return <LoadingSpinner label="Loading property…" />;

  return (
    <div className="mx-auto max-w-3xl px-4 py-8 sm:px-6">
      <h1 className="text-2xl font-bold text-neutral-900">{isEdit ? "Edit property" : "List a new property"}</h1>

      <form onSubmit={handleSubmit} className="mt-6 flex flex-col gap-5 rounded-2xl border border-neutral-200 bg-white p-5">
        {saveMutation.isError && <ErrorBanner message={extractErrorMessage(saveMutation.error)} />}

        <div>
          <label className="mb-1 block text-sm font-medium text-neutral-700">Title</label>
          <input
            required
            value={form.title}
            onChange={(e) => update("title", e.target.value)}
            className="w-full rounded-lg border border-neutral-300 px-3 py-2 text-sm"
            placeholder="e.g. Sunset Beach Villa"
          />
        </div>

        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className="mb-1 block text-sm font-medium text-neutral-700">Property type</label>
            <select
              value={form.propertyType}
              onChange={(e) => update("propertyType", e.target.value as PropertyType)}
              className="w-full rounded-lg border border-neutral-300 px-3 py-2 text-sm"
            >
              {PROPERTY_TYPES.map((t) => (
                <option key={t} value={t}>
                  {t.replace("_", " ")}
                </option>
              ))}
            </select>
          </div>
          <div>
            <label className="mb-1 block text-sm font-medium text-neutral-700">Room type</label>
            <select
              value={form.roomType}
              onChange={(e) => update("roomType", e.target.value as RoomType)}
              className="w-full rounded-lg border border-neutral-300 px-3 py-2 text-sm"
            >
              {ROOM_TYPES.map((t) => (
                <option key={t} value={t}>
                  {t.replace("_", " ")}
                </option>
              ))}
            </select>
          </div>
        </div>

        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className="mb-1 block text-sm font-medium text-neutral-700">City</label>
            <input
              required
              value={form.city}
              onChange={(e) => update("city", e.target.value)}
              className="w-full rounded-lg border border-neutral-300 px-3 py-2 text-sm"
            />
          </div>
          <div>
            <label className="mb-1 block text-sm font-medium text-neutral-700">Country</label>
            <input
              required
              value={form.country}
              onChange={(e) => update("country", e.target.value)}
              className="w-full rounded-lg border border-neutral-300 px-3 py-2 text-sm"
            />
          </div>
          <div>
            <label className="mb-1 block text-sm font-medium text-neutral-700">State</label>
            <input
              value={form.state}
              onChange={(e) => update("state", e.target.value)}
              className="w-full rounded-lg border border-neutral-300 px-3 py-2 text-sm"
            />
          </div>
          <div>
            <label className="mb-1 block text-sm font-medium text-neutral-700">Address</label>
            <input
              value={form.addressLine}
              onChange={(e) => update("addressLine", e.target.value)}
              className="w-full rounded-lg border border-neutral-300 px-3 py-2 text-sm"
            />
          </div>
        </div>

        <div className="grid grid-cols-2 gap-4 sm:grid-cols-4">
          <div>
            <label className="mb-1 block text-sm font-medium text-neutral-700">Price/night</label>
            <input
              type="number"
              min={0}
              required
              value={form.pricePerNight}
              onChange={(e) => update("pricePerNight", Number(e.target.value))}
              className="w-full rounded-lg border border-neutral-300 px-3 py-2 text-sm"
            />
          </div>
          <div>
            <label className="mb-1 block text-sm font-medium text-neutral-700">Cleaning fee</label>
            <input
              type="number"
              min={0}
              value={form.cleaningFee}
              onChange={(e) => update("cleaningFee", Number(e.target.value))}
              className="w-full rounded-lg border border-neutral-300 px-3 py-2 text-sm"
            />
          </div>
          <div>
            <label className="mb-1 block text-sm font-medium text-neutral-700">Max guests</label>
            <input
              type="number"
              min={1}
              required
              value={form.maxGuests}
              onChange={(e) => update("maxGuests", Number(e.target.value))}
              className="w-full rounded-lg border border-neutral-300 px-3 py-2 text-sm"
            />
          </div>
          <div>
            <label className="mb-1 block text-sm font-medium text-neutral-700">Bathrooms</label>
            <input
              type="number"
              min={0}
              step="0.5"
              value={form.bathrooms}
              onChange={(e) => update("bathrooms", Number(e.target.value))}
              className="w-full rounded-lg border border-neutral-300 px-3 py-2 text-sm"
            />
          </div>
          <div>
            <label className="mb-1 block text-sm font-medium text-neutral-700">Bedrooms</label>
            <input
              type="number"
              min={0}
              value={form.bedrooms}
              onChange={(e) => update("bedrooms", Number(e.target.value))}
              className="w-full rounded-lg border border-neutral-300 px-3 py-2 text-sm"
            />
          </div>
          <div>
            <label className="mb-1 block text-sm font-medium text-neutral-700">Beds</label>
            <input
              type="number"
              min={0}
              value={form.beds}
              onChange={(e) => update("beds", Number(e.target.value))}
              className="w-full rounded-lg border border-neutral-300 px-3 py-2 text-sm"
            />
          </div>
        </div>

        <div>
          <label className="mb-1 block text-sm font-medium text-neutral-700">Amenities</label>
          <div className="grid grid-cols-2 gap-1 sm:grid-cols-3">
            {amenitiesQuery.data?.map((a) => (
              <label key={a.id} className="flex items-center gap-2 text-sm text-neutral-700">
                <input
                  type="checkbox"
                  checked={(form.amenityIds ?? []).includes(a.id)}
                  onChange={() => toggleAmenity(a.id)}
                />
                {a.name}
              </label>
            ))}
          </div>
        </div>

        <div>
          <div className="flex items-center justify-between">
            <label className="mb-1 block text-sm font-medium text-neutral-700">Description</label>
            <button
              type="button"
              onClick={handleGenerateDescription}
              disabled={aiLoading}
              className="flex items-center gap-1.5 rounded-full border border-brand-200 bg-brand-50 px-3 py-1 text-xs font-medium text-brand-700 hover:bg-brand-100 disabled:opacity-50"
            >
              <Sparkles size={13} /> {aiLoading ? "Generating…" : "Generate with AI"}
            </button>
          </div>
          <textarea
            value={form.description}
            onChange={(e) => update("description", e.target.value)}
            rows={6}
            className="w-full rounded-lg border border-neutral-300 px-3 py-2 text-sm"
            placeholder="Describe your property, or click Generate with AI"
          />
          {aiError && <p className="mt-1 text-xs text-red-600">{aiError}</p>}
        </div>

        <div>
          <label className="mb-1 block text-sm font-medium text-neutral-700">
            {isEdit ? "Add more photos" : "Photos"}
          </label>
          <label className="flex cursor-pointer items-center gap-2 rounded-lg border border-dashed border-neutral-300 px-3 py-4 text-sm text-neutral-500 hover:border-brand-400">
            <Upload size={16} />
            Click to select images
            <input
              type="file"
              accept="image/*"
              multiple
              className="hidden"
              onChange={(e) => setNewImages(Array.from(e.target.files ?? []))}
            />
          </label>
          {newImages.length > 0 && (
            <div className="mt-2 flex flex-wrap gap-2">
              {newImages.map((f, i) => (
                <span key={i} className="flex items-center gap-1 rounded-full bg-neutral-100 px-2.5 py-1 text-xs text-neutral-600">
                  {f.name}
                  <button type="button" onClick={() => setNewImages((prev) => prev.filter((_, idx) => idx !== i))}>
                    <X size={12} />
                  </button>
                </span>
              ))}
            </div>
          )}
        </div>

        <button
          type="submit"
          disabled={saveMutation.isPending}
          className="rounded-full bg-brand-600 py-2.5 text-sm font-semibold text-white hover:bg-brand-700 disabled:opacity-50"
        >
          {saveMutation.isPending ? "Saving…" : isEdit ? "Save changes" : "List property"}
        </button>
      </form>
    </div>
  );
}
