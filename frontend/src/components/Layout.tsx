import { Outlet } from "react-router-dom";
import { Navbar } from "./Navbar";
import { Footer } from "./Footer";
import { AiChatWidget } from "./AiChatWidget";
import { useAuth } from "../context/AuthContext";

export function Layout() {
  const { user } = useAuth();
  return (
    <div className="flex min-h-screen flex-col">
      <Navbar />
      <main className="flex-1">
        <Outlet />
      </main>
      <Footer />
      {user && <AiChatWidget />}
    </div>
  );
}
