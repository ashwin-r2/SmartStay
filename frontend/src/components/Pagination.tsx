interface PaginationProps {
  page: number;
  totalPages: number;
  onPageChange: (page: number) => void;
}

export function Pagination({ page, totalPages, onPageChange }: PaginationProps) {
  if (totalPages <= 1) return null;

  return (
    <div className="flex items-center justify-center gap-2 py-6">
      <button
        className="rounded-lg border border-neutral-300 px-3 py-1.5 text-sm disabled:opacity-40"
        disabled={page <= 0}
        onClick={() => onPageChange(page - 1)}
      >
        Previous
      </button>
      <span className="text-sm text-neutral-500">
        Page {page + 1} of {totalPages}
      </span>
      <button
        className="rounded-lg border border-neutral-300 px-3 py-1.5 text-sm disabled:opacity-40"
        disabled={page >= totalPages - 1}
        onClick={() => onPageChange(page + 1)}
      >
        Next
      </button>
    </div>
  );
}
