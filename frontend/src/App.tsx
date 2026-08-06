import { Route, Routes } from "react-router-dom";
import { Layout } from "./components/Layout";
import { ProtectedRoute } from "./components/ProtectedRoute";

import { Home } from "./pages/Home";
import { SearchResults } from "./pages/SearchResults";
import { PropertyDetails } from "./pages/PropertyDetails";
import { Login } from "./pages/Login";
import { Register } from "./pages/Register";
import { NotFound } from "./pages/NotFound";

import { MyBookings } from "./pages/guest/MyBookings";
import { BookingDetails } from "./pages/guest/BookingDetails";
import { TripPlanner } from "./pages/guest/TripPlanner";
import { BudgetPlanner } from "./pages/guest/BudgetPlanner";

import { HostDashboard } from "./pages/host/HostDashboard";
import { PropertyForm } from "./pages/host/PropertyForm";
import { PropertyCalendar } from "./pages/host/PropertyCalendar";

import { AdminDashboard } from "./pages/admin/AdminDashboard";
import { ManageUsers } from "./pages/admin/ManageUsers";
import { ManageProperties } from "./pages/admin/ManageProperties";
import { ManageAmenities } from "./pages/admin/ManageAmenities";
import { Analytics } from "./pages/admin/Analytics";
import { AiStats } from "./pages/admin/AiStats";

function App() {
  return (
    <Routes>
      <Route element={<Layout />}>
        <Route index element={<Home />} />
        <Route path="search" element={<SearchResults />} />
        <Route path="properties/:id" element={<PropertyDetails />} />
        <Route path="login" element={<Login />} />
        <Route path="register" element={<Register />} />

        <Route
          path="trip-planner"
          element={
            <ProtectedRoute>
              <TripPlanner />
            </ProtectedRoute>
          }
        />
        <Route
          path="budget-planner"
          element={
            <ProtectedRoute>
              <BudgetPlanner />
            </ProtectedRoute>
          }
        />
        <Route
          path="bookings"
          element={
            <ProtectedRoute>
              <MyBookings />
            </ProtectedRoute>
          }
        />
        <Route
          path="bookings/:id"
          element={
            <ProtectedRoute>
              <BookingDetails />
            </ProtectedRoute>
          }
        />

        <Route
          path="host"
          element={
            <ProtectedRoute roles={["HOST", "ADMIN"]}>
              <HostDashboard />
            </ProtectedRoute>
          }
        />
        <Route
          path="host/properties/new"
          element={
            <ProtectedRoute roles={["HOST", "ADMIN"]}>
              <PropertyForm />
            </ProtectedRoute>
          }
        />
        <Route
          path="host/properties/:id/edit"
          element={
            <ProtectedRoute roles={["HOST", "ADMIN"]}>
              <PropertyForm />
            </ProtectedRoute>
          }
        />
        <Route
          path="host/properties/:id/calendar"
          element={
            <ProtectedRoute roles={["HOST", "ADMIN"]}>
              <PropertyCalendar />
            </ProtectedRoute>
          }
        />

        <Route
          path="admin"
          element={
            <ProtectedRoute roles={["ADMIN"]}>
              <AdminDashboard />
            </ProtectedRoute>
          }
        />
        <Route
          path="admin/users"
          element={
            <ProtectedRoute roles={["ADMIN"]}>
              <ManageUsers />
            </ProtectedRoute>
          }
        />
        <Route
          path="admin/properties"
          element={
            <ProtectedRoute roles={["ADMIN"]}>
              <ManageProperties />
            </ProtectedRoute>
          }
        />
        <Route
          path="admin/amenities"
          element={
            <ProtectedRoute roles={["ADMIN"]}>
              <ManageAmenities />
            </ProtectedRoute>
          }
        />
        <Route
          path="admin/analytics"
          element={
            <ProtectedRoute roles={["ADMIN"]}>
              <Analytics />
            </ProtectedRoute>
          }
        />
        <Route
          path="admin/ai-usage"
          element={
            <ProtectedRoute roles={["ADMIN"]}>
              <AiStats />
            </ProtectedRoute>
          }
        />

        <Route path="*" element={<NotFound />} />
      </Route>
    </Routes>
  );
}

export default App;
