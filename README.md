# JobTracker

Aplicación para organizar una búsqueda de empleo: guardas cada oferta, sigues en qué estado va (guardada → enviada → entrevista → oferta o rechazo) y ves tus estadísticas, como la tasa de respuesta.

La estoy construyendo por etapas mientras busco trabajo, así que también la uso de verdad.

![Java](https://img.shields.io/badge/Java-21-ED8B00?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.1-6DB33F?logo=springboot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-4169E1?logo=postgresql&logoColor=white)
![React](https://img.shields.io/badge/React-19-20232A?logo=react&logoColor=61DAFB)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?logo=docker&logoColor=white)

![Tablero de JobTracker con datos de ejemplo](docs/tablero.png)
<sub>Tablero con datos de ejemplo (empresas ficticias).</sub>

## Hoja de ruta

| Etapa | Qué incluye | Estado |
|---|---|---|
| 1 | API REST con Spring Boot y PostgreSQL, validaciones, pruebas y documentación Swagger | ✅ Lista |
| 2 | Frontend en React: tablero Kanban con arrastrar y soltar, estadísticas, búsqueda y formulario | ✅ Lista |
| 3 | Login con Spring Security y JWT; cada usuario ve solo sus postulaciones | ⏳ Siguiente |
| 4 | Extensión de Chrome para guardar ofertas con un clic | Pendiente |
| 5 | IA que compara la hoja de vida con cada oferta | Pendiente |
| 6 | Integración continua con GitHub Actions y despliegue | Pendiente |

## API (etapa 1)

![Swagger UI de JobTracker](docs/swagger.png)

| Método | Ruta | Qué hace |
|---|---|---|
| `GET` | `/api/applications?status=&q=` | Lista las postulaciones, de la más nueva a la más antigua. Filtra por estado o por texto en empresa y cargo |
| `GET` | `/api/applications/{id}` | Devuelve una postulación |
| `POST` | `/api/applications` | Crea una postulación (`201` con cabecera `Location`) |
| `PUT` | `/api/applications/{id}` | Reemplaza una postulación |
| `PATCH` | `/api/applications/{id}/status` | Cambia solo el estado; es lo que usará el tablero al arrastrar una tarjeta |
| `DELETE` | `/api/applications/{id}` | Borra una postulación (`204`) |
| `GET` | `/api/applications/stats` | Totales por estado, postulaciones enviadas y tasa de respuesta |

**Decisiones técnicas:**
- **Paquetes por funcionalidad** (`jobapplication/`) en lugar de por capa, para tener junto todo lo de una misma funcionalidad.
- **DTOs con `record`**: la API nunca expone la entidad JPA directamente.
- **Flyway** crea y versiona el esquema; Hibernate solo valida que las entidades coincidan con él (`ddl-auto=validate`).
- **Errores con el formato estándar RFC 9457** (`ProblemDetail`). Los errores de validación incluyen un mapa `errors` (campo → mensaje) para que el frontend muestre cada mensaje bajo su campo.
- **Filtros combinables** con `Specification` de Spring Data JPA.
- **Pruebas de integración con Testcontainers**: corren contra un PostgreSQL real en Docker, la misma versión que en producción, no contra una base en memoria.

## Frontend (etapa 2)

- **Tablero Kanban** con cinco columnas. Las tarjetas se arrastran con el ratón, con el dedo (manteniendo pulsado) y con el teclado (espacio para tomar, flechas para mover), gracias a [dnd-kit](https://dndkit.com/). Los movimientos se anuncian al lector de pantalla en español.
- **Actualización optimista:** al soltar una tarjeta, el tablero cambia al instante y, si la API falla, la tarjeta vuelve a su sitio con un aviso.
- **Formulario** en un `<dialog>` nativo que muestra cada error de validación de la API debajo de su campo.
- **Búsqueda** por empresa o cargo, que espera a que dejes de escribir antes de consultar la API.
- **Estadísticas:** total, enviadas, entrevistas, ofertas y tasa de respuesta.
- En el celular, las columnas se deslizan de lado una a una.

## Cómo ejecutarlo

Necesitas **Docker**. Desde la raíz del proyecto:

```bash
docker compose up --build
```

- **Tablero: http://localhost:5173**
- API: http://localhost:8081
- Documentación interactiva (Swagger): http://localhost:8081/swagger-ui.html
- Estado del servicio: http://localhost:8081/actuator/health

La API usa el puerto 8081 porque en Windows el 8080 suele estar ocupado por XAMPP/Apache.

### Desarrollo del frontend

Con la API corriendo (`docker compose up db api`):

```bash
cd frontend
npm install
npm run dev
```

### Pruebas

```bash
cd backend
./mvnw test
```

Requieren Docker en marcha (Testcontainers levanta PostgreSQL por su cuenta).

## Estructura

```
jobtracker/
├── docker-compose.yml          ← PostgreSQL + API + tablero
├── frontend/src/
│   ├── components/             ← tablero, columnas, tarjetas, formulario y estadísticas
│   ├── useApplications.js      ← estado y llamadas a la API
│   └── api.js                  ← cliente HTTP
└── backend/
    └── src/main/java/com/alejandrojimenez/jobtracker/
        ├── jobapplication/     ← entidad, repositorio, servicio, controlador y DTOs
        ├── common/             ← manejo global de errores
        └── config/             ← CORS y Swagger
```

## Autor

**Alejandro Jiménez Pinzón** · [Portafolio](https://alejandro-jimenez-portafolio.netlify.app/) · [LinkedIn](https://www.linkedin.com/in/alejandro-jimenez-pinzon/) · [GitHub](https://github.com/Alejo-developer)
