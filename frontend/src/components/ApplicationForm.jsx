import { useEffect, useRef, useState } from "react";
import { STATUSES } from "../statuses.js";

const EMPTY = { company: "", position: "", jobUrl: "", location: "", source: "", status: "SAVED", appliedOn: "", notes: "" };
const SOURCES = ["LinkedIn", "Computrabajo", "elempleo", "Indeed", "Magneto", "Referido", "Página de la empresa"];

const today = () => new Date().toLocaleDateString("en-CA"); // YYYY-MM-DD in local time

/**
 * Create/edit form in a native modal dialog. Field errors come from the API's
 * validation response and are shown under each field.
 */
export default function ApplicationForm({ application, onSave, onDelete, onClose }) {
  const dialogRef = useRef(null);
  const editing = Boolean(application?.id);
  const [values, setValues] = useState(() => toFormValues(application));
  const [errors, setErrors] = useState({});
  const [formError, setFormError] = useState(null);
  const [saving, setSaving] = useState(false);
  const [confirmingDelete, setConfirmingDelete] = useState(false);

  useEffect(() => {
    // StrictMode runs effects twice in development; showModal() throws if it is already open
    if (!dialogRef.current.open) dialogRef.current.showModal();
  }, []);

  function change(event) {
    const { name, value } = event.target;
    setValues((current) => ({ ...current, [name]: value }));
    setErrors((current) => ({ ...current, [name]: undefined }));
  }

  async function submit(event) {
    event.preventDefault();
    setSaving(true);
    setFormError(null);
    try {
      await onSave({ ...values, appliedOn: values.appliedOn || null });
      onClose();
    } catch (e) {
      setErrors(e.fieldErrors ?? {});
      setFormError(Object.keys(e.fieldErrors ?? {}).length ? "Revisa los campos marcados." : e.message);
    } finally {
      setSaving(false);
    }
  }

  async function remove() {
    if (!confirmingDelete) {
      setConfirmingDelete(true);
      return;
    }
    try {
      await onDelete(application.id);
      onClose();
    } catch (e) {
      setFormError(e.message);
    }
  }

  const field = (name) => ({
    id: `field-${name}`,
    name,
    value: values[name],
    onChange: change,
    "aria-invalid": Boolean(errors[name]),
    "aria-describedby": errors[name] ? `error-${name}` : undefined,
  });
  const error = (name) =>
    errors[name] && (
      <p className="field-error" id={`error-${name}`}>
        {errors[name]}
      </p>
    );

  return (
    <dialog ref={dialogRef} className="form-dialog" onClose={onClose} aria-labelledby="form-title">
      <form onSubmit={submit} noValidate>
        <h2 id="form-title">{editing ? "Editar postulación" : "Nueva postulación"}</h2>

        <div className="form-grid">
          <div className="field">
            <label htmlFor="field-company">Empresa *</label>
            <input {...field("company")} autoFocus maxLength={120} />
            {error("company")}
          </div>
          <div className="field">
            <label htmlFor="field-position">Cargo *</label>
            <input {...field("position")} maxLength={120} />
            {error("position")}
          </div>
          <div className="field field-wide">
            <label htmlFor="field-jobUrl">Enlace de la oferta</label>
            <input {...field("jobUrl")} type="url" placeholder="https://" maxLength={500} />
            {error("jobUrl")}
          </div>
          <div className="field">
            <label htmlFor="field-location">Ubicación</label>
            <input {...field("location")} placeholder="Bogotá, remoto…" maxLength={120} />
            {error("location")}
          </div>
          <div className="field">
            <label htmlFor="field-source">Dónde la encontraste</label>
            <input {...field("source")} list="sources" maxLength={60} />
            <datalist id="sources">
              {SOURCES.map((source) => (
                <option key={source} value={source} />
              ))}
            </datalist>
            {error("source")}
          </div>
          <div className="field">
            <label htmlFor="field-status">Estado</label>
            <select {...field("status")}>
              {STATUSES.map((status) => (
                <option key={status.id} value={status.id}>
                  {status.label}
                </option>
              ))}
            </select>
          </div>
          <div className="field">
            <label htmlFor="field-appliedOn">Fecha de postulación</label>
            <input {...field("appliedOn")} type="date" max={today()} />
            {error("appliedOn")}
          </div>
          <div className="field field-wide">
            <label htmlFor="field-notes">Notas</label>
            <textarea {...field("notes")} rows={3} maxLength={2000} placeholder="Salario, contacto, próximos pasos…" />
            {error("notes")}
          </div>
        </div>

        {formError && (
          <p className="form-error" role="alert">
            {formError}
          </p>
        )}

        <div className="form-actions">
          {editing && (
            <button type="button" className="button button-danger" onClick={remove}>
              {confirmingDelete ? "Sí, eliminar" : "Eliminar"}
            </button>
          )}
          {editing && application.jobUrl && (
            <a className="button button-ghost" href={application.jobUrl} target="_blank" rel="noreferrer">
              Ver oferta
            </a>
          )}
          <span className="form-actions-spacer" />
          <button type="button" className="button button-ghost" onClick={() => dialogRef.current.close()}>
            Cancelar
          </button>
          <button type="submit" className="button button-primary" disabled={saving}>
            {saving ? "Guardando…" : editing ? "Guardar cambios" : "Guardar postulación"}
          </button>
        </div>
      </form>
    </dialog>
  );
}

function toFormValues(application) {
  if (!application) return EMPTY;
  // The API returns null for empty optional fields; inputs need empty strings
  return Object.fromEntries(Object.keys(EMPTY).map((key) => [key, application[key] ?? EMPTY[key]]));
}
