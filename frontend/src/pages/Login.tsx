import { useState } from "react";
import { useForm } from "react-hook-form";
import { Link, useLocation, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { extractErrorMessage } from "../api/client";
import { ErrorBanner } from "../components/ErrorBanner";
import type { LoginRequest } from "../types";

export function Login() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [serverError, setServerError] = useState<string | null>(null);
  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<LoginRequest>();

  const from = (location.state as { from?: Location })?.from?.pathname ?? "/";

  const onSubmit = async (data: LoginRequest) => {
    setServerError(null);
    try {
      await login(data);
      navigate(from, { replace: true });
    } catch (err) {
      setServerError(extractErrorMessage(err, "Invalid email or password."));
    }
  };

  return (
    <div className="mx-auto flex max-w-md flex-col px-4 py-16">
      <h1 className="text-2xl font-bold text-neutral-900">Welcome back</h1>
      <p className="mt-1 text-sm text-neutral-500">Log in to book stays, chat with the AI assistant, and more.</p>

      <form onSubmit={handleSubmit(onSubmit)} className="mt-6 flex flex-col gap-4">
        {serverError && <ErrorBanner message={serverError} />}
        <div>
          <label className="mb-1 block text-sm font-medium text-neutral-700">Email</label>
          <input
            type="email"
            {...register("email", { required: "Email is required" })}
            className="w-full rounded-lg border border-neutral-300 px-3 py-2 text-sm"
            placeholder="you@example.com"
          />
          {errors.email && <p className="mt-1 text-xs text-red-600">{errors.email.message}</p>}
        </div>
        <div>
          <label className="mb-1 block text-sm font-medium text-neutral-700">Password</label>
          <input
            type="password"
            {...register("password", { required: "Password is required" })}
            className="w-full rounded-lg border border-neutral-300 px-3 py-2 text-sm"
            placeholder="••••••••"
          />
          {errors.password && <p className="mt-1 text-xs text-red-600">{errors.password.message}</p>}
        </div>
        <button
          type="submit"
          disabled={isSubmitting}
          className="mt-2 rounded-full bg-brand-600 py-2.5 text-sm font-semibold text-white hover:bg-brand-700 disabled:opacity-50"
        >
          {isSubmitting ? "Logging in…" : "Log in"}
        </button>
      </form>

      <p className="mt-6 text-center text-sm text-neutral-500">
        Don't have an account?{" "}
        <Link to="/register" className="font-medium text-brand-600 hover:underline">
          Sign up
        </Link>
      </p>

      <div className="mt-8 rounded-xl border border-neutral-200 bg-neutral-50 p-4 text-xs text-neutral-500">
        <p className="font-semibold text-neutral-600">Demo accounts (seeded sample data):</p>
        <p>Guest: guest.alice@staysmart.ai / Password123!</p>
        <p>Host: aarav.host@staysmart.ai / Host12345!</p>
        <p>Admin: admin@staysmart.ai / Admin12345!</p>
      </div>
    </div>
  );
}
