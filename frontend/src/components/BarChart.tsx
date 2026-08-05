interface BarDatum {
  label: string;
  value: number;
}

/**
 * Minimal horizontal bar chart: one sequential hue (magnitude only, no categorical
 * identity to encode), direct value labels instead of a separate axis, and a
 * recessive track so the bar itself carries the data-ink.
 */
export function BarChart({
  data,
  formatValue = (v) => v.toLocaleString(),
  barClassName = "bg-brand-500",
}: {
  data: BarDatum[];
  formatValue?: (v: number) => string;
  barClassName?: string;
}) {
  const max = Math.max(1, ...data.map((d) => d.value));

  if (data.length === 0) {
    return <p className="text-sm text-neutral-400">No data yet.</p>;
  }

  return (
    <div className="flex flex-col gap-2.5" role="img" aria-label="Bar chart">
      {data.map((d) => (
        <div key={d.label} className="flex items-center gap-3 text-sm">
          <span className="w-28 shrink-0 truncate text-neutral-600">{d.label}</span>
          <div className="h-3 flex-1 overflow-hidden rounded-full bg-neutral-100">
            <div
              className={`h-full rounded-full ${barClassName}`}
              style={{ width: `${(d.value / max) * 100}%` }}
            />
          </div>
          <span className="w-16 shrink-0 text-right font-medium text-neutral-800">{formatValue(d.value)}</span>
        </div>
      ))}
    </div>
  );
}
