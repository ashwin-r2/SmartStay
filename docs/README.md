# StaySmart AI — Project Overview

StaySmart AI is a full-stack, Airbnb-inspired vacation rental platform with seven
integrated AI features, built as a production-quality Project Based Learning (PBL)
submission for a 2nd-year B.Tech IT program.

## Table of contents

- [Features](#features)
- [Tech stack](#tech-stack)
- [Architecture](#architecture)
- [Folder structure](#folder-structure)
- [Diagrams & references](#diagrams--references)
- [Quick start](#quick-start)

## Features

### 1. User Authentication
Registration, login, stateless JWT (access + rotating refresh tokens), three roles
(`USER`, `HOST`, `ADMIN`), BCrypt password hashing.

### 2. Property Management
CRUD listings, multi-image upload, an amenity catalog, location fields (address/city/
state/country/coordinates), and a per-property availability calendar (booked ranges
auto-blocked, plus manual host blocks for maintenance).

### 3. Booking System
Filtered search (city, dates, guests, price, property/room type, amenities, keyword),
instant-book confirmation with server-side overlap checking, booking history, and
ownership-checked cancellation that releases the calendar block.

### 4. Review System
Star ratings + comments + photos, gated to one review per **completed** booking, with
the property's cached average rating/review count kept in sync automatically.

### 5. AI Modules (LangChain4j + OpenAI)
| Feature | What it does |
|---|---|
| **Property Recommendation** | Builds a candidate pool from top-rated properties (optionally scoped to a guest's inferred preferred city) and asks the LLM to explain the picks |
| **Review Summarization** | Condenses a property's written reviews into overall sentiment / pros / cons / verdict |
| **Chat Assistant** | Multi-turn concierge, conversation history persisted per session |
| **Trip Planner** | Day-by-day itinerary for a destination, date range, party size, interests and budget level |
| **Property Description Generator** | Drafts a listing description from structured property details; a host can apply it directly |
| **Budget Planner** | Category-by-category trip budget, grounded in the platform's real average nightly price for the destination |
| **Smart Search** | Parses a natural-language query into structured filters, then reuses the exact same search code path as the regular filter UI |

Every AI call is logged (feature, success/failure, latency, tokens where available) to
power the admin AI usage dashboard. If no `OPENAI_API_KEY` is configured, AI endpoints
return a clear "not configured" error instead of crashing — the rest of the platform
stays fully usable offline for grading/demo purposes.

### 6. Admin Dashboard
Platform-wide stats, user management (role change, enable/disable, delete), property
management (list/delete any listing), booking analytics (status breakdown, revenue,
monthly trend), and AI usage statistics (calls/success-rate/latency per feature).

## Tech stack

**Backend:** Java 21 · Spring Boot 3 · Spring Security (JWT) · Spring Data JPA ·
Hibernate · Maven · PostgreSQL · Flyway · LangChain4j + OpenAI · springdoc-openapi ·
Lombok · JUnit 5 · Mockito · H2 (tests)

**Frontend:** React 19 · TypeScript · Vite · Tailwind CSS v4 · React Router ·
TanStack React Query · Axios · React Hook Form

## Architecture

The backend is organized as a modular monolith — one Java package per business
capability, each with the same internal layering:

```
controller  →  service  →  repository  →  entity
    ↓             ↓
   dto      (validation, business rules, @Transactional)
```

Cross-cutting concerns live in shared packages:
- `common` — base entity (audit fields), `ApiResponse`/`PageResponse` envelopes
- `security` — JWT issuing/validation, the authentication filter
- `config` — Spring Security, CORS, OpenAPI, JPA auditing, static file serving, the
  LangChain4j `ChatLanguageModel` bean (with a graceful no-API-key fallback)
- `exception` — typed domain exceptions + one `@RestControllerAdvice` translating
  every failure into a consistent JSON error shape

Modules depend on each other only through their **service** classes (never entities/
repositories directly across module boundaries) — e.g. `BookingService` calls
`PropertyService.isAvailable(...)` rather than querying `AvailabilityBlockRepository`
itself. This keeps ownership of each table's invariants inside its own module.

The frontend is a single-page app: `api/*.ts` are thin typed wrappers around the REST
endpoints, `AuthContext` owns the session, `App.tsx` composes protected/role-gated
routes around a shared `Layout` (nav + footer + the floating AI chat widget).

## Folder structure

```
StaySmart AI/
├── backend/
│   ├── src/main/java/com/staysmart/
│   │   ├── config/          security, CORS, OpenAPI, AI wiring
│   │   ├── security/        JWT service + filter
│   │   ├── common/           base entity, response envelopes
│   │   ├── exception/        global handler + typed exceptions
│   │   ├── user/              entity, repo, service, controller, dto
│   │   ├── property/          entity, repo, service, controller, dto, specification
│   │   ├── booking/           entity, repo, service, controller, dto
│   │   ├── review/             entity, repo, service, controller, dto
│   │   ├── ai/                 entity, repo, client (LangChain4j), service, controller, dto
│   │   └── admin/              service, controller, dto
│   ├── src/main/resources/
│   │   ├── application*.yml
│   │   └── db/migration/       Flyway: V1 schema, V2 seed data
│   ├── src/test/java/...       JUnit 5 + Mockito unit tests
│   └── Dockerfile
├── frontend/
│   ├── src/
│   │   ├── api/                axios client + per-module API wrappers
│   │   ├── types/               shared TS types mirroring backend DTOs
│   │   ├── context/AuthContext.tsx
│   │   ├── components/          Navbar, Layout, PropertyCard, AiChatWidget, ...
│   │   └── pages/                public, guest/, host/, admin/
│   └── Dockerfile
├── docs/                        this folder
├── docker-compose.yml
└── .env.example
```

## Diagrams & references

- [`ER-DIAGRAM.md`](ER-DIAGRAM.md) — entity-relationship diagram (Mermaid)
- [`UML-CLASS-DIAGRAM.md`](UML-CLASS-DIAGRAM.md) — domain model + layered architecture (Mermaid)
- [`SEQUENCE-DIAGRAMS.md`](SEQUENCE-DIAGRAMS.md) — auth/JWT, booking, AI chat, AI review summary (Mermaid)
- [`DATABASE-SCHEMA.md`](DATABASE-SCHEMA.md) — table-by-table schema reference
- [`API-DOCUMENTATION.md`](API-DOCUMENTATION.md) — REST endpoint reference (+ live Swagger UI)
- [`DEPLOYMENT-GUIDE.md`](DEPLOYMENT-GUIDE.md) — Docker Compose & manual setup, env vars
- [`sample-data.sql`](sample-data.sql) — the seeded demo dataset

## Quick start

See [`DEPLOYMENT-GUIDE.md`](DEPLOYMENT-GUIDE.md). Short version:

```bash
cp .env.example .env    # fill in DB password, JWT secret, optional OPENAI_API_KEY
docker compose up --build
```

Then open `http://localhost:5173` and log in with one of the seeded demo accounts
(shown on the Login page) — guest, host, and admin.
