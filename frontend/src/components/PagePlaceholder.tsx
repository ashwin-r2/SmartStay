/** Temporary placeholder used while a page is being built out; replaced page-by-page. */
export function PagePlaceholder({ title }: { title: string }) {
  return (
    <div className="mx-auto max-w-3xl px-4 py-24 text-center">
      <h1 className="text-2xl font-semibold text-neutral-900">{title}</h1>
      <p className="mt-2 text-neutral-500">This page is under construction.</p>
    </div>
  );
}
