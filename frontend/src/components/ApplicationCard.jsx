import { useDraggable } from "@dnd-kit/core";

const shortDate = new Intl.DateTimeFormat("es-CO", { day: "numeric", month: "short", timeZone: "UTC" });

/** Card content, shared by the card on the board and the copy that follows the pointer while dragging. */
export function CardContent({ application }) {
  const details = [application.location, application.source].filter(Boolean).join(" · ");
  return (
    <>
      <p className="card-company">{application.company}</p>
      <p className="card-position">{application.position}</p>
      {(details || application.appliedOn) && (
        <p className="card-meta">
          {details}
          {details && application.appliedOn && " · "}
          {application.appliedOn && (
            <time dateTime={application.appliedOn}>{shortDate.format(new Date(application.appliedOn))}</time>
          )}
        </p>
      )}
    </>
  );
}

export default function ApplicationCard({ application, onOpen }) {
  const { attributes, listeners, setNodeRef, isDragging } = useDraggable({
    id: application.id,
    data: { status: application.status },
  });

  return (
    <li>
      <button
        ref={setNodeRef}
        type="button"
        className={`card ${isDragging ? "card-dragging" : ""}`}
        onClick={() => onOpen(application)}
        aria-label={`${application.company}, ${application.position}. Pulsa para editar o arrastra para cambiar de estado.`}
        {...listeners}
        {...attributes}
      >
        <CardContent application={application} />
      </button>
    </li>
  );
}
