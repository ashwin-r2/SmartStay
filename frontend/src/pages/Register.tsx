import { useState } from "react";
import { useForm } from "react-hook-form";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { extractErrorMessage } from "../api/client";
import { ErrorBanner } from "../components/ErrorBanner";
import type { RegisterRequest } from "../types";

export function Register() {
  const { register: registerUser } = useAuth();
  const navigate = useNavigate();
  const [serverError, setServerError] = useState<string | null>(null);
  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<RegisterRequest>({ defaultValues: { role: "USER" } });

  const onSubmit = async (data: RegisterRequest) => {
    setServerError(null);
    try {
      await registerUser(data);
      navigate("/", { replace: true });
    } catch (err) {
      setServerError(extractErrorMessage(err, "Could not create your account."));
    }
  };

  return (
    <div className="mx-auto flex max-w-md flex-col px-4 py-16">
      <h1 className="text-2xl font-bold text-neutral-900">Create your account</h1>
      <p className="mt-1 text-sm text-neutral-500">Book unique stays or start hosting your own property.</p>

      <form onSubmit={handleSubmit(onSubmit)} className="mt-6 flex flex-col gap-4">
        {serverError && <ErrorBanner message={serverError} />}
        <div>
          <label className="mb-1 block text-sm font-medium text-neutral-700">Full name</label>
          <input
            {...register("fullName", { required: "Full name is required" })}
            className="w-full rounded-lg border border-neutral-300 px-3 py-2 text-sm"
            placeholder="Jane Doe"
          />
          {errors.fullName && <p className="mt-1 text-xs text-red-600">{errors.fullName.message}</p>}
        </div>
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
            {...register("password", {
              required: "Password is required",
              minLength: { value: 8, message: "At least 8 characters" },
            })}
            className="w-full rounded-lg border border-neutral-300 px-3 py-2 text-sm"
            placeholder="At least 8 characters, with a number"
          />
          {errors.password && <p className="mt-1 text-xs text-red-600">{errors.password.message}</p>}
        </div>
        <div>
          <label className="mb-1 block text-sm font-medium text-neutral-700">I want to</label>
          <select {...register("role")} className="w-full rounded-lg border border-neutral-300 px-3 py-2 text-sm">
            <option value="USER">Book stays as a guest</option>
            <option value="HOST">Host my property</option>
          </select>
        </div>
        <button
          type="submit"
          disabled={isSubmitting}
          className="mt-2 rounded-full bg-brand-600 py-2.5 text-sm font-semibold text-white hover:bg-brand-700 disabled:opacity-50"
        >
          {isSubmitting ? "Creating account…" : "Sign up"}
        </button>
      </form>

      <p className="mt-6 text-center text-sm text-neutral-500">
        Already have an account?{" "}
        <Link to="/login" className="font-medium text-brand-600 hover:underline">
          Log in
        </Link>
      </p>
    </div>
  );
}
