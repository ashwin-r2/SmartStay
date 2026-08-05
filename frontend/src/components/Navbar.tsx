import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { Menu, X, Home as HomeIcon } from "lucide-react";
import { useAuth } from "../context/AuthContext";

export function Navbar() {
  const { user, logout, hasRole } = useAuth();
  const navigate = useNavigate();
  const [open, setOpen] = useState(false);

  const handleLogout = () => {
    logout();
    setOpen(false);
    navigate("/");
  };

  return (
    <header className="sticky top-0 z-40 border-b border-neutral-200 bg-white/90 backdrop-blur">
      <div className="mx-auto flex max-w-7xl items-center justify-between px-4 py-3 sm:px-6">
        <Link to="/" className="flex items-center gap-2 text-lg font-bold text-brand-600">
          <HomeIcon size={22} />
          StaySmart <span className="text-neutral-900">AI</span>
        </Link>

        <nav className="hidden items-center gap-6 text-sm font-medium text-neutral-700 md:flex">
          <Link to="/search" className="hover:text-brand-600">Explore</Link>
          <Link to="/trip-planner" className="hover:text-brand-600">Trip Planner</Link>
          <Link to="/budget-planner" className="hover:text-brand-600">Budget Planner</Link>
          {hasRole("HOST", "ADMIN") && (
            <Link to="/host" className="hover:text-brand-600">Host Dashboard</Link>
          )}
          {hasRole("ADMIN") && <Link to="/admin" className="hover:text-brand-600">Admin</Link>}
        </nav>

        <div className="hidden items-center gap-3 md:flex">
          {user ? (
            <>
              <Link to="/bookings" className="text-sm font-medium text-neutral-700 hover:text-brand-600">
                My Bookings
              </Link>
              <span className="text-sm text-neutral-500">Hi, {user.fullName.split(" ")[0]}</span>
              <button
                onClick={handleLogout}
                className="rounded-full bg-neutral-900 px-4 py-2 text-sm font-medium text-white hover:bg-neutral-700"
              >
                Log out
              </button>
            </>
          ) : (
            <>
              <Link to="/login" className="text-sm font-medium text-neutral-700 hover:text-brand-600">
                Log in
              </Link>
              <Link
                to="/register"
                className="rounded-full bg-brand-600 px-4 py-2 text-sm font-medium text-white hover:bg-brand-700"
              >
                Sign up
              </Link>
            </>
          )}
        </div>

        <button className="md:hidden" onClick={() => setOpen((v) => !v)} aria-label="Toggle menu">
          {open ? <X /> : <Menu />}
        </button>
      </div>

      {open && (
        <div className="border-t border-neutral-200 px-4 py-3 md:hidden">
          <div className="flex flex-col gap-3 text-sm font-medium text-neutral-700">
            <Link to="/search" onClick={() => setOpen(false)}>Explore</Link>
            <Link to="/trip-planner" onClick={() => setOpen(false)}>Trip Planner</Link>
            <Link to="/budget-planner" onClick={() => setOpen(false)}>Budget Planner</Link>
            {hasRole("HOST", "ADMIN") && <Link to="/host" onClick={() => setOpen(false)}>Host Dashboard</Link>}
            {hasRole("ADMIN") && <Link to="/admin" onClick={() => setOpen(false)}>Admin</Link>}
            {user ? (
              <>
                <Link to="/bookings" onClick={() => setOpen(false)}>My Bookings</Link>
                <button onClick={handleLogout} className="text-left text-red-600">
                  Log out
                </button>
              </>
            ) : (
              <>
                <Link to="/login" onClick={() => setOpen(false)}>Log in</Link>
                <Link to="/register" onClick={() => setOpen(false)}>Sign up</Link>
              </>
            )}
          </div>
        </div>
      )}
    </header>
  );
}
