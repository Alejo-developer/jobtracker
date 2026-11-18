import { useEffect, useState } from "react";
import Board from "./components/Board.jsx";
import StatsBar from "./components/StatsBar.jsx";
import ApplicationForm from "./components/ApplicationForm.jsx";
import { useApplications } from "./useApplications.js";
import { API_URL } from "./api.js";

/** Waits until the user stops typing before searching, to avoid one request per key. */
function useDebounced(value, delay) {
  const [debounced, setDebounced] = useState(value);
  useEffect(() => {
    const timer = setTimeout(() => setDebounced(value), delay);
    return () => clearTimeout(timer);
  }, [value, delay]);
  return debounced;
}

export default function App() {
  const [search, setSearch] = useState("");
  const debouncedSearch = useDebounced(search.trim(), 300);
  const { applications, stats, loading, error, reload, dismissError, create, update, move, remove } =
    useApplications(debouncedSearch);
  // null: form closed · {}: new application · application: editing it
  const [editing, setEditing] = useState(null);

  const cannotConnect = error?.status === 0 && applications.length === 0;

  return (
    <div className="app">
      <header className="header">
        <h1 className="logo">
          <span className="logo-mark" aria-hidden="true" />
          JobTracker
        </h1>
        <input
          className="search"
          type="search"
          placeholder="Buscar empresa o cargo"
          aria-label="Buscar por empresa o cargo"
          value={search}
          onChange={(event) => setSearch(event.target.value)}
        />
        <button type="button" className="button button-primary" onClick={() => setEditing({})}>
          Nueva postulación
        </button>
      </header>

      <main>
        {error && !cannotConnect && (
          <div className="banner" role="alert">
            <span>{error.message}</span>
            <button type="button" className="button button-ghost" onClick={dismissError}>
              Cerrar
            </button>
          </div>
        )}

        {cannotConnect ? (
          <div className="message">
            <h2>No se pudo conectar con la API</h2>
            <p>
              El frontend busca la API en <code>{API_URL}</code>. Levántala con <code>docker compose up</code> y vuelve
              a intentarlo.
            </p>
            <button type="button" className="button button-primary" onClick={reload}>
              Reintentar
            </button>
          </div>
        ) : loading ? (
          <p className="message">Cargando postulaciones…</p>
        ) : (
          <>
            <StatsBar stats={stats} />
            {debouncedSearch && applications.length === 0 && (
              <p className="search-empty">Ninguna postulación coincide con «{debouncedSearch}».</p>
            )}
            <Board applications={applications} onMove={move} onOpen={setEditing} />
          </>
        )}
      </main>

      {editing && (
        <ApplicationForm
          application={editing.id ? editing : null}
          onSave={(data) => (editing.id ? update(editing.id, data) : create(data))}
          onDelete={remove}
          onClose={() => setEditing(null)}
        />
      )}
    </div>
  );
}
