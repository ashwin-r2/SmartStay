# StaySmart AI 🏡🤖

**StaySmart AI** is a full-stack, Airbnb-inspired vacation rental platform with integrated AI
features, built as a production-quality Project Based Learning (PBL) submission for a 2nd-year
B.Tech IT program.

> This README is the quick entry point; the full write-up (features, architecture, folder
> structure, diagrams) lives in [`docs/README.md`](docs/README.md).
>
> **Verified:** `mvn test` (backend, JUnit/Mockito) and `npm run build` (frontend) both pass,
> and the full stack has been smoke-tested end-to-end via `docker compose up` against a real
> PostgreSQL database — see [`docs/DEPLOYMENT-GUIDE.md`](docs/DEPLOYMENT-GUIDE.md).

## Tech stack

| Layer      | Technology |
|------------|------------|
| Backend    | Java 21, Spring Boot 3, Spring Security, Spring Data JPA, Maven |
| Database   | PostgreSQL, Flyway migrations |
| Auth       | JWT (access + refresh), BCrypt, role-based access (`USER`, `HOST`, `ADMIN`) |
| AI         | LangChain4j + OpenAI API |
| Frontend   | React 19, TypeScript, Vite, Tailwind CSS v4, React Router, React Query |
| Docs/API   | springdoc-openapi (Swagger UI), Markdown + Mermaid diagrams |
| Testing    | JUnit 5, Mockito, H2 (test scope) |

## Repository layout

```
StaySmart AI/
├── backend/     Spring Boot REST API
├── frontend/    React + Tailwind SPA
├── docs/        ER diagram, UML, sequence diagrams, API docs, deployment guide
└── docker-compose.yml
```

## Quick start

See [`docs/DEPLOYMENT-GUIDE.md`](docs/DEPLOYMENT-GUIDE.md) for full instructions (Docker Compose
one-liner, manual local dev, environment variables). Short version:

```bash
# 1. Copy env template and fill in DB + OpenAI credentials
cp .env.example .env

# 2. Start everything (Postgres + backend + frontend)
docker compose up --build

# Backend:  http://localhost:8080/api  (Swagger UI at /api/swagger-ui.html)
# Frontend: http://localhost:5173
```

## Modules

1. **User Authentication** — registration, login, JWT, roles (USER/HOST/ADMIN)
2. **Property Management** — CRUD, image upload, amenities, location, availability calendar
3. **Booking System** — search, book, booking history, cancellation
4. **Review System** — ratings, comments, images
5. **AI Modules** — recommendations, review summarization, chat assistant, trip planner,
   description generator, budget planner, smart search
6. **Admin Dashboard** — manage users/properties, booking analytics, AI usage statistics

## License

Educational project — built for coursework purposes.
