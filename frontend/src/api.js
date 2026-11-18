// Thin wrapper around fetch for the JobTracker API.

export const API_URL = import.meta.env.VITE_API_URL ?? "http://localhost:8081";

/** Error with the HTTP status and, for validation errors, a field → message map. */
export class ApiError extends Error {
  constructor(message, status, fieldErrors = {}) {
    super(message);
    this.status = status;
    this.fieldErrors = fieldErrors;
  }
}

async function request(path, { method = "GET", body } = {}) {
  let response;
  try {
    response = await fetch(`${API_URL}/api/applications${path}`, {
      method,
      headers: body ? { "Content-Type": "application/json" } : undefined,
      body: body ? JSON.stringify(body) : undefined,
    });
  } catch {
    throw new ApiError(`No se pudo conectar con la API en ${API_URL}.`, 0);
  }

  if (response.status === 204) return null;
  const data = await response.json().catch(() => null);
  if (!response.ok) {
    // The API answers errors as RFC 9457 problem details
    throw new ApiError(data?.detail ?? `Error ${response.status}`, response.status, data?.errors ?? {});
  }
  return data;
}

export const api = {
  list: (search) => request(search ? `?q=${encodeURIComponent(search)}` : ""),
  stats: () => request("/stats"),
  create: (application) => request("", { method: "POST", body: application }),
  update: (id, application) => request(`/${id}`, { method: "PUT", body: application }),
  updateStatus: (id, status) => request(`/${id}/status`, { method: "PATCH", body: { status } }),
  remove: (id) => request(`/${id}`, { method: "DELETE" }),
};
