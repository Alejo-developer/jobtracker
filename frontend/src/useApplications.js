import { useCallback, useEffect, useState } from "react";
import { api } from "./api.js";

/**
 * Loads the applications (filtered by the search text) and the stats, and exposes the
 * actions that change them. Moving a card updates the screen first and rolls back if
 * the API fails, so dragging feels instant.
 */
export function useApplications(search) {
  const [applications, setApplications] = useState([]);
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const refreshStats = useCallback(() => api.stats().then(setStats).catch(() => {}), []);

  const load = useCallback(async () => {
    setError(null);
    try {
      const [list, newStats] = await Promise.all([api.list(search), api.stats()]);
      setApplications(list);
      setStats(newStats);
    } catch (e) {
      setError(e);
    } finally {
      setLoading(false);
    }
  }, [search]);

  useEffect(() => {
    load();
  }, [load]);

  async function create(data) {
    const created = await api.create(data);
    setApplications((current) => [created, ...current]);
    refreshStats();
    return created;
  }

  async function update(id, data) {
    const updated = await api.update(id, data);
    setApplications((current) => current.map((a) => (a.id === id ? updated : a)));
    refreshStats();
    return updated;
  }

  async function move(id, status) {
    const previous = applications;
    setApplications((current) => current.map((a) => (a.id === id ? { ...a, status } : a)));
    try {
      await api.updateStatus(id, status);
      refreshStats();
    } catch (e) {
      setApplications(previous);
      setError(e);
    }
  }

  async function remove(id) {
    await api.remove(id);
    setApplications((current) => current.filter((a) => a.id !== id));
    refreshStats();
  }

  return { applications, stats, loading, error, reload: load, dismissError: () => setError(null), create, update, move, remove };
}
