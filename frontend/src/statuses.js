// Board columns, in the order an application usually moves through them.
export const STATUSES = [
  { id: "SAVED", label: "Guardadas", hint: "Guarda aquí las ofertas que te interesan." },
  { id: "APPLIED", label: "Enviadas", hint: "Arrastra aquí las ofertas a las que ya enviaste tu hoja de vida." },
  { id: "INTERVIEW", label: "Entrevista", hint: "Cuando te llamen a entrevista, muévela aquí." },
  { id: "OFFER", label: "Oferta", hint: "Aquí llegan las ofertas de trabajo." },
  { id: "REJECTED", label: "Rechazadas", hint: "Las que no siguieron. También cuentan como respuesta." },
];

export const statusLabel = (id) => STATUSES.find((status) => status.id === id)?.label ?? id;
