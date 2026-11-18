import { useDroppable } from "@dnd-kit/core";
import ApplicationCard from "./ApplicationCard.jsx";

export default function Column({ status, applications, onOpen }) {
  const { setNodeRef, isOver } = useDroppable({ id: status.id });
  const headingId = `column-${status.id}`;

  return (
    <section
      ref={setNodeRef}
      className={`column column-${status.id.toLowerCase()} ${isOver ? "column-over" : ""}`}
      aria-labelledby={headingId}
    >
      <h2 id={headingId} className="column-title">
        {status.label}
        <span className="column-count">{applications.length}</span>
      </h2>
      {applications.length === 0 ? (
        <p className="column-empty">{status.hint}</p>
      ) : (
        <ul className="column-cards">
          {applications.map((application) => (
            <ApplicationCard key={application.id} application={application} onOpen={onOpen} />
          ))}
        </ul>
      )}
    </section>
  );
}
