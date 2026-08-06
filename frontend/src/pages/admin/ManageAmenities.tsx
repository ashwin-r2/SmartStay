import { useState } from "react";
import type { FormEvent } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { Pencil, Trash2, X, Check } from "lucide-react";
import { amenitiesApi } from "../../api/properties";
import { LoadingSpinner } from "../../components/LoadingSpinner";
import { ErrorBanner } from "../../components/ErrorBanner";
import { extractErrorMessage } from "../../api/client";

export function ManageAmenities() {
  const queryClient = useQueryClient();
  const query = useQuery({ queryKey: ["amenities"], queryFn: amenitiesApi.list });

  const [newName, setNewName] = useState("");
  const [newIcon, setNewIcon] = useState("");
  const [editingId, setEditingId] = useState<number | null>(null);
  const [editName, setEditName] = useState("");
  const [editIcon, setEditIcon] = useState("");
  const [formError, setFormError] = useState<string | null>(null);

  const invalidate = () => queryClient.invalidateQueries({ queryKey: ["amenities"] });

  const createMutation = useMutation({
    mutationFn: () => amenitiesApi.create({ name: newName.trim(), icon: newIcon.trim() || undefined }),
    onSuccess: () => {
      setNewName("");
      setNewIcon("");
      setFormError(null);
      invalidate();
    },
    onError: (err) => setFormError(extractErrorMessage(err)),
  });

  const updateMutation = useMutation({
    mutationFn: ({ id, name, icon }: { id: number; name: string; icon?: string }) =>
      amenitiesApi.update(id, { name, icon }),
    onSuccess: () => {
      setEditingId(null);
      setFormError(null);
      invalidate();
    },
    onError: (err) => setFormError(extractErrorMessage(err)),
  });

  const deleteMutation = useMutation({
    mutationFn: (id: number) => amenitiesApi.remove(id),
    onSuccess: invalidate,
  });

  const startEdit = (id: number, name: string, icon?: string) => {
    setEditingId(id);
    setEditName(name);
    setEditIcon(icon ?? "");
    setFormError(null);
  };

  const submitCreate = (e: FormEvent) => {
    e.preventDefault();
    if (!newName.trim()) return;
    createMutation.mutate();
  };

  const submitEdit = (e: FormEvent) => {
    e.preventDefault();
    if (editingId === null || !editName.trim()) return;
    updateMutation.mutate({ id: editingId, name: editName.trim(), icon: editIcon.trim() || undefined });
  };

  return (
    <div className="mx-auto max-w-3xl px-4 py-8 sm:px-6">
      <h1 className="text-2xl font-bold text-neutral-900">Manage Amenities</h1>
      <p className="mt-1 text-sm text-neutral-500">
        This is the shared catalog hosts pick from when listing a property.
      </p>

      <form onSubmit={submitCreate} className="mt-6 flex flex-wrap items-end gap-3 rounded-2xl border border-neutral-200 bg-white p-4">
        <div className="flex-1 min-w-[160px]">
          <label className="mb-1 block text-xs font-medium text-neutral-700">Name</label>
          <input
            value={newName}
            onChange={(e) => setNewName(e.target.value)}
            placeholder="e.g. EV Charger"
            className="w-full rounded-lg border border-neutral-300 px-3 py-2 text-sm"
          />
        </div>
        <div className="w-40">
          <label className="mb-1 block text-xs font-medium text-neutral-700">Icon (optional)</label>
          <input
            value={newIcon}
            onChange={(e) => setNewIcon(e.target.value)}
            placeholder="e.g. plug-zap"
            className="w-full rounded-lg border border-neutral-300 px-3 py-2 text-sm"
          />
        </div>
        <button
          type="submit"
          disabled={!newName.trim() || createMutation.isPending}
          className="rounded-lg bg-brand-600 px-4 py-2 text-sm font-medium text-white hover:bg-brand-700 disabled:opacity-50"
        >
          {createMutation.isPending ? "Adding…" : "Add amenity"}
        </button>
      </form>

      {formError && (
        <div className="mt-3">
          <ErrorBanner message={formError} />
        </div>
      )}

      {query.isLoading && <LoadingSpinner label="Loading amenities…" />}
      {query.isError && <ErrorBanner message={extractErrorMessage(query.error)} />}

      {query.data && (
        <div className="mt-4 divide-y divide-neutral-100 rounded-2xl border border-neutral-200 bg-white">
          {query.data.map((a) => (
            <div key={a.id} className="flex items-center justify-between gap-3 px-4 py-3">
              {editingId === a.id ? (
                <form onSubmit={submitEdit} className="flex flex-1 flex-wrap items-center gap-2">
                  <input
                    value={editName}
                    onChange={(e) => setEditName(e.target.value)}
                    autoFocus
                    className="min-w-[140px] flex-1 rounded-lg border border-neutral-300 px-2 py-1 text-sm"
                  />
                  <input
                    value={editIcon}
                    onChange={(e) => setEditIcon(e.target.value)}
                    placeholder="icon"
                    className="w-32 rounded-lg border border-neutral-300 px-2 py-1 text-sm"
                  />
                  <button
                    type="submit"
                    disabled={!editName.trim() || updateMutation.isPending}
                    className="rounded-full p-1.5 text-green-600 hover:bg-green-50 disabled:opacity-50"
                    aria-label="Save"
                  >
                    <Check size={16} />
                  </button>
                  <button
                    type="button"
                    onClick={() => setEditingId(null)}
                    className="rounded-full p-1.5 text-neutral-500 hover:bg-neutral-100"
                    aria-label="Cancel"
                  >
                    <X size={16} />
                  </button>
                </form>
              ) : (
                <>
                  <div>
                    <span className="font-medium text-neutral-900">{a.name}</span>
                    {a.icon && <span className="ml-2 text-xs text-neutral-400">{a.icon}</span>}
                  </div>
                  <div className="flex items-center gap-3">
                    <button
                      onClick={() => startEdit(a.id, a.name, a.icon)}
                      className="text-neutral-500 hover:text-brand-600"
                      aria-label={`Edit ${a.name}`}
                    >
                      <Pencil size={15} />
                    </button>
                    <button
                      onClick={() => {
                        if (confirm(`Delete amenity "${a.name}"?`)) deleteMutation.mutate(a.id);
                      }}
                      className="text-neutral-500 hover:text-red-600"
                      aria-label={`Delete ${a.name}`}
                    >
                      <Trash2 size={15} />
                    </button>
                  </div>
                </>
              )}
            </div>
          ))}

          {query.data.length === 0 && (
            <p className="px-4 py-6 text-center text-sm text-neutral-500">No amenities yet — add the first one above.</p>
          )}
        </div>
      )}
    </div>
  );
}
