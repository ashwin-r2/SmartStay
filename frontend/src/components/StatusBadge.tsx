const STYLES: Record<string, string> = {
  CONFIRMED: "bg-green-50 text-green-700 border-green-200",
  PENDING: "bg-amber-50 text-amber-700 border-amber-200",
  CANCELLED: "bg-red-50 text-red-700 border-red-200",
  COMPLETED: "bg-neutral-100 text-neutral-600 border-neutral-200",
  ACTIVE: "bg-green-50 text-green-700 border-green-200",
  INACTIVE: "bg-neutral-100 text-neutral-500 border-neutral-200",
};

export function StatusBadge({ status }: { status: string }) {
  return (
    <span className={`inline-block rounded-full border px-2.5 py-0.5 text-xs font-medium ${STYLES[status] ?? "bg-neutral-100 text-neutral-600 border-neutral-200"}`}>
      {status}
    </span>
  );
}
