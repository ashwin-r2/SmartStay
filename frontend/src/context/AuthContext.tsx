import { createContext, useCallback, useContext, useEffect, useMemo, useState, type ReactNode } from "react";
import { authApi } from "../api/auth";
import { tokenStorage } from "../api/client";
import type { LoginRequest, RegisterRequest, Role, UserResponse } from "../types";

interface AuthContextValue {
  user: UserResponse | null;
  loading: boolean;
  login: (payload: LoginRequest) => Promise<void>;
  register: (payload: RegisterRequest) => Promise<void>;
  logout: () => void;
  refreshProfile: () => Promise<void>;
  hasRole: (...roles: Role[]) => boolean;
}

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<UserResponse | null>(null);
  const [loading, setLoading] = useState(true);

  const loadProfile = useCallback(async () => {
    if (!tokenStorage.getAccessToken()) {
      setUser(null);
      setLoading(false);
      return;
    }
    try {
      const profile = await authApi.me();
      setUser(profile);
    } catch {
      tokenStorage.clear();
      setUser(null);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadProfile();
  }, [loadProfile]);

  useEffect(() => {
    const handleUnauthorized = () => {
      tokenStorage.clear();
      setUser(null);
    };
    window.addEventListener("staysmart:unauthorized", handleUnauthorized);
    return () => window.removeEventListener("staysmart:unauthorized", handleUnauthorized);
  }, []);

  const login = useCallback(async (payload: LoginRequest) => {
    const auth = await authApi.login(payload);
    tokenStorage.setTokens(auth.accessToken, auth.refreshToken);
    setUser(auth.user);
  }, []);

  const register = useCallback(async (payload: RegisterRequest) => {
    const auth = await authApi.register(payload);
    tokenStorage.setTokens(auth.accessToken, auth.refreshToken);
    setUser(auth.user);
  }, []);

  const logout = useCallback(() => {
    const refreshToken = tokenStorage.getRefreshToken();
    if (refreshToken) {
      authApi.logout(refreshToken).catch(() => undefined);
    }
    tokenStorage.clear();
    setUser(null);
  }, []);

  const hasRole = useCallback((...roles: Role[]) => !!user && roles.includes(user.role), [user]);

  const value = useMemo<AuthContextValue>(
    () => ({ user, loading, login, register, logout, refreshProfile: loadProfile, hasRole }),
    [user, loading, login, register, logout, loadProfile, hasRole]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth(): AuthContextValue {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth must be used within an AuthProvider");
  return ctx;
}
