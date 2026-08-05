export function LoadingSpinner({ label = "Loading…" }: { label?: string }) {
  return (
    <div className="flex flex-col items-center justify-center gap-3 py-16 text-neutral-500">
      <div className="h-8 w-8 animate-spin rounded-full border-2 border-neutral-200 border-t-brand-500" />
      <span className="text-sm">{label}</span>
    </div>
  );
}
