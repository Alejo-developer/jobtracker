const percent = new Intl.NumberFormat("es-CO", { style: "percent", maximumFractionDigits: 0 });

export default function StatsBar({ stats }) {
  if (!stats) return null;
  const items = [
    { label: "Postulaciones", value: stats.total },
    { label: "Enviadas", value: stats.sent },
    { label: "Entrevistas", value: stats.byStatus.INTERVIEW },
    { label: "Ofertas", value: stats.byStatus.OFFER },
    {
      label: "Tasa de respuesta",
      value: percent.format(stats.responseRate),
      detail: "De las enviadas, cuántas tuvieron respuesta",
    },
  ];

  return (
    <dl className="stats" aria-label="Resumen">
      {items.map((item) => (
        <div key={item.label} className="stat" title={item.detail}>
          <dt>{item.label}</dt>
          <dd>{item.value}</dd>
        </div>
      ))}
    </dl>
  );
}
