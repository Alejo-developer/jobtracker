import { useState } from "react";
import {
  DndContext,
  DragOverlay,
  KeyboardSensor,
  PointerSensor,
  TouchSensor,
  useSensor,
  useSensors,
} from "@dnd-kit/core";
import Column from "./Column.jsx";
import { CardContent } from "./ApplicationCard.jsx";
import { STATUSES, statusLabel } from "../statuses.js";

// Screen reader messages while dragging with the keyboard (space to pick up, arrows to move)
const accessibility = {
  screenReaderInstructions: {
    draggable: "Pulsa espacio para tomar la tarjeta, usa las flechas para moverla y espacio otra vez para soltarla.",
  },
  announcements: {
    onDragStart: () => "Tarjeta tomada.",
    onDragOver: ({ over }) => (over ? `Sobre la columna ${statusLabel(over.id)}.` : "Fuera de las columnas."),
    onDragEnd: ({ over }) => (over ? `Movida a ${statusLabel(over.id)}.` : "Movimiento cancelado."),
    onDragCancel: () => "Movimiento cancelado.",
  },
};

export default function Board({ applications, onMove, onOpen }) {
  const [dragged, setDragged] = useState(null);

  const sensors = useSensors(
    // A small movement threshold keeps a simple click as "open the card"
    useSensor(PointerSensor, { activationConstraint: { distance: 6 } }),
    // On touch screens, press and hold briefly so swiping still scrolls the board
    useSensor(TouchSensor, { activationConstraint: { delay: 200, tolerance: 6 } }),
    useSensor(KeyboardSensor),
  );

  function handleDragEnd({ active, over }) {
    setDragged(null);
    if (over && over.id !== active.data.current.status) {
      onMove(active.id, over.id);
    }
  }

  return (
    <DndContext
      sensors={sensors}
      accessibility={accessibility}
      onDragStart={({ active }) => setDragged(applications.find((a) => a.id === active.id))}
      onDragEnd={handleDragEnd}
      onDragCancel={() => setDragged(null)}
    >
      <div className="board">
        {STATUSES.map((status) => (
          <Column
            key={status.id}
            status={status}
            applications={applications.filter((a) => a.status === status.id)}
            onOpen={onOpen}
          />
        ))}
      </div>
      <DragOverlay>
        {dragged && (
          <div className="card card-overlay">
            <CardContent application={dragged} />
          </div>
        )}
      </DragOverlay>
    </DndContext>
  );
}
