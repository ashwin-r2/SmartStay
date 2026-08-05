export function Footer() {
  return (
    <footer className="mt-16 border-t border-neutral-200 bg-white">
      <div className="mx-auto max-w-7xl px-4 py-8 text-sm text-neutral-500 sm:px-6">
        <div className="flex flex-col items-center justify-between gap-3 sm:flex-row">
          <p>© {new Date().getFullYear()} StaySmart AI. Built for educational purposes.</p>
          <p>Java 21 · Spring Boot · React · Tailwind CSS · LangChain4j</p>
        </div>
      </div>
    </footer>
  );
}
