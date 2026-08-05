import { Link } from "react-router-dom";

export function NotFound() {
  return (
    <div className="mx-auto flex max-w-xl flex-col items-center px-4 py-24 text-center">
      <h1 className="text-6xl font-bold text-brand-600">404</h1>
      <p className="mt-4 text-lg text-neutral-600">We couldn't find that page.</p>
      <Link to="/" className="mt-6 rounded-full bg-neutral-900 px-5 py-2.5 text-sm font-medium text-white">
        Back to Home
      </Link>
    </div>
  );
}
