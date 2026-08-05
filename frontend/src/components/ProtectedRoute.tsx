import type { ReactNode } from "react";
import { Navigate, useLocation } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { LoadingSpinner } from "./LoadingSpinner";
import type { Role } from "../types";

/** Requires the user to be logged in; optionally also requires one of `roles`. */
export function ProtectedRoute({ children, roles }: { children: ReactNode; roles?: Role[] }) {
  const { user, loading } = useAuth();
  const location = useLocation();

  if (loading) return <LoadingSpinner label="Checking your session…" />;

  if (!user) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  if (roles && !roles.includes(user.role)) {
    return <Navigate to="/" replace />;
  }

  return <>{children}</>;
}
