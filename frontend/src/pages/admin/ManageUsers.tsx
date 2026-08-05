import { useState } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { adminApi } from "../../api/admin";
import { LoadingSpinner } from "../../components/LoadingSpinner";
import { ErrorBanner } from "../../components/ErrorBanner";
import { Pagination } from "../../components/Pagination";
import { extractErrorMessage } from "../../api/client";
import type { Role } from "../../types";

const ROLES: Role[] = ["USER", "HOST", "ADMIN"];

export function ManageUsers() {
  const [roleFilter, setRoleFilter] = useState<Role | "">("");
  const [page, setPage] = useState(0);
  const queryClient = useQueryClient();

  const query = useQuery({
    queryKey: ["admin", "users", roleFilter, page],
    queryFn: () => adminApi.users(roleFilter || undefined, page, 20),
  });

  const invalidate = () => queryClient.invalidateQueries({ queryKey: ["admin", "users"] });

  const toggleEnabledMutation = useMutation({
    mutationFn: ({ id, enabled }: { id: number; enabled: boolean }) => adminApi.setUserEnabled(id, enabled),
    onSuccess: invalidate,
  });

  const changeRoleMutation = useMutation({
    mutationFn: ({ id, role }: { id: number; role: Role }) => adminApi.changeUserRole(id, role),
    onSuccess: invalidate,
  });

  const deleteMutation = useMutation({
    mutationFn: (id: number) => adminApi.deleteUser(id),
    onSuccess: invalidate,
  });

  return (
    <div className="mx-auto max-w-6xl px-4 py-8 sm:px-6">
      <h1 className="text-2xl font-bold text-neutral-900">Manage Users</h1>

      <div className="mt-4 flex gap-2 text-sm">
        <button
          onClick={() => setRoleFilter("")}
          className={`rounded-full border px-3 py-1.5 ${roleFilter === "" ? "border-neutral-900 bg-neutral-900 text-white" : "border-neutral-300"}`}
        >
          All
        </button>
        {ROLES.map((r) => (
          <button
            key={r}
            onClick={() => setRoleFilter(r)}
            className={`rounded-full border px-3 py-1.5 ${roleFilter === r ? "border-neutral-900 bg-neutral-900 text-white" : "border-neutral-300"}`}
          >
            {r}
          </button>
        ))}
      </div>

      {query.isLoading && <LoadingSpinner label="Loading users…" />}
      {query.isError && <ErrorBanner message={extractErrorMessage(query.error)} />}

      {query.data && (
        <div className="mt-4 overflow-x-auto rounded-2xl border border-neutral-200 bg-white">
          <table className="w-full text-left text-sm">
            <thead className="border-b border-neutral-200 text-xs uppercase text-neutral-500">
              <tr>
                <th className="px-4 py-3">Name</th>
                <th className="px-4 py-3">Email</th>
                <th className="px-4 py-3">Role</th>
                <th className="px-4 py-3">Status</th>
                <th className="px-4 py-3">Actions</th>
              </tr>
            </thead>
            <tbody>
              {query.data.content.map((u) => (
                <tr key={u.id} className="border-b border-neutral-100 last:border-0">
                  <td className="px-4 py-3 font-medium text-neutral-900">{u.fullName}</td>
                  <td className="px-4 py-3 text-neutral-600">{u.email}</td>
                  <td className="px-4 py-3">
                    <select
                      value={u.role}
                      onChange={(e) => changeRoleMutation.mutate({ id: u.id, role: e.target.value as Role })}
                      className="rounded-lg border border-neutral-300 px-2 py-1 text-xs"
                    >
                      {ROLES.map((r) => (
                        <option key={r} value={r}>
                          {r}
                        </option>
                      ))}
                    </select>
                  </td>
                  <td className="px-4 py-3">
                    <span className={`rounded-full px-2 py-0.5 text-xs font-medium ${u.enabled ? "bg-green-50 text-green-700" : "bg-neutral-100 text-neutral-500"}`}>
                      {u.enabled ? "Enabled" : "Disabled"}
                    </span>
                  </td>
                  <td className="px-4 py-3">
                    <div className="flex gap-3 text-xs">
                      <button
                        onClick={() => toggleEnabledMutation.mutate({ id: u.id, enabled: !u.enabled })}
                        className="text-neutral-600 hover:text-brand-600"
                      >
                        {u.enabled ? "Disable" : "Enable"}
                      </button>
                      <button
                        onClick={() => {
                          if (confirm(`Delete user ${u.fullName}?`)) deleteMutation.mutate(u.id);
                        }}
                        className="text-red-500 hover:text-red-700"
                      >
                        Delete
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {query.data && <Pagination page={query.data.page} totalPages={query.data.totalPages} onPageChange={setPage} />}
    </div>
  );
}
