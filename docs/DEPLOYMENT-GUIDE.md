# Deployment Guide

## Option A — Docker Compose (recommended, one command)

Prerequisites: Docker + Docker Compose.

```bash
# From the repo root
cp .env.example .env
# Edit .env: set a real DB password, JWT_SECRET, and (optionally) OPENAI_API_KEY

docker compose up --build
```

This starts three containers:

| Service | Container | Exposed at |
|---|---|---|
| PostgreSQL 16 | `staysmart-postgres` | `localhost:5432` |
| Spring Boot backend | `staysmart-backend` | `http://localhost:8080/api` (Swagger UI at `/api/swagger-ui.html`) |
| React frontend (built + served by nginx) | `staysmart-frontend` | `http://localhost:5173` |

Flyway runs automatically on backend startup, creating the schema and loading the sample
data (see [`DATABASE-SCHEMA.md`](DATABASE-SCHEMA.md), [`sample-data.sql`](sample-data.sql)).
Uploaded images persist in the `uploads` named Docker volume.

To stop: `docker compose down` (add `-v` to also drop the database volume and start fresh).

## Option B — Manual local development

Prerequisites: **Java 21**, **Maven 3.9+**, **Node.js 20+**, a local **PostgreSQL 16** instance.

### 1. Database

```bash
createdb staysmart
createuser staysmart --pwprompt   # set password to match DB_PASSWORD below
```

### 2. Backend

```bash
cd backend
export DB_URL=jdbc:postgresql://localhost:5432/staysmart
export DB_USERNAME=staysmart
export DB_PASSWORD=<your-password>
export JWT_SECRET=$(openssl rand -base64 48)
export OPENAI_API_KEY=sk-...          # optional — omit to run without live AI calls
export SPRING_PROFILES_ACTIVE=dev

mvn spring-boot:run
```

The backend starts on `http://localhost:8080/api`, running Flyway migrations (schema +
sample data) automatically on first boot.

### 3. Frontend

```bash
cd frontend
cp .env.example .env      # VITE_API_BASE_URL=/api works out of the box in dev
npm install
npm run dev
```

Vite's dev server (`http://localhost:5173`) proxies `/api/**` to `http://localhost:8080`
(see `vite.config.ts`), so no CORS configuration is needed for local development.

## Environment variables reference

| Variable | Default | Description |
|---|---|---|
| `DB_URL` / `DB_USERNAME` / `DB_PASSWORD` | `jdbc:postgresql://localhost:5432/staysmart` / `staysmart` / `staysmart` | PostgreSQL connection |
| `JWT_SECRET` | *(dev placeholder — change in prod)* | HMAC signing key, ≥ 32 bytes |
| `JWT_ACCESS_EXPIRATION_MS` | `3600000` (1h) | Access token lifetime |
| `JWT_REFRESH_EXPIRATION_MS` | `1209600000` (14d) | Refresh token lifetime |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:5173` | Comma-separated allowed origins |
| `UPLOADS_DIR` | `uploads` | Local disk path for uploaded images |
| `AI_PROVIDER` | `openai` | `openai`, `gemini`, or `claude` — selects which LangChain4j client `AiConfig` wires up |
| `OPENAI_API_KEY` | *(empty)* | Enables real AI responses when `AI_PROVIDER=openai`; omit to run AI-free |
| `OPENAI_CHAT_MODEL` | `gpt-4o-mini` | Any OpenAI chat-completion model |
| `GEMINI_API_KEY` | *(empty)* | Enables real AI responses when `AI_PROVIDER=gemini` — get one at [aistudio.google.com/apikey](https://aistudio.google.com/apikey) |
| `GEMINI_CHAT_MODEL` | `gemini-3.6-flash` | Any Gemini chat-completion model |
| `ANTHROPIC_API_KEY` | *(empty)* | Enables real AI responses when `AI_PROVIDER=claude` — get one at [console.anthropic.com/settings/keys](https://console.anthropic.com/settings/keys) |
| `ANTHROPIC_CHAT_MODEL` | `claude-haiku-4-5` | Any Claude chat-completion model |
| `SPRING_PROFILES_ACTIVE` | `dev` | `dev` \| `prod` \| `test` |
| `VITE_API_BASE_URL` (frontend) | `/api` | Override if the backend isn't reverse-proxied under the same origin |

## Manual smoke-test flow

Whichever option you use, this end-to-end pass exercises every module:

1. Open the frontend, **Sign up** as a guest, or **Log in** with a seeded demo account
   (shown right on the Login page: `guest.alice@staysmart.ai` / `Password123!`).
2. **Explore** → search a city (try "Goa") → open a property → **Book now**.
3. Log in as a host (`aarav.host@staysmart.ai` / `Host12345!`) → **Host Dashboard** →
   add a new property, try **Generate with AI** on the description field, upload a photo.
4. Back as the guest: **My Bookings** → open a past/completed booking → leave a review.
5. Try the **AI Trip Planner**, **AI Budget Planner**, the natural-language search box on
   Home, and the floating **AI chat assistant** (bottom-right, once logged in).
6. Log in as admin (`admin@staysmart.ai` / `Admin12345!`) → **Admin** → check the
   dashboard, Manage Users/Properties, Booking Analytics, and AI Usage Statistics.

Steps 5–6's AI panels only return live model output when `OPENAI_API_KEY` is set; without
it they surface a clear "AI service is temporarily unavailable" message rather than
crashing, so the rest of the app remains fully gradable offline.

## Production notes

- **Uploads**: `ImageStorageService` writes to local disk by default. For a real
  deployment, swap it for an S3/Cloud Storage-backed implementation (same interface,
  one class to change) and point `app.uploads.base-url` at a CDN/bucket URL instead of
  the local static-file handler.
- **Secrets**: never commit a real `JWT_SECRET` or `OPENAI_API_KEY` — `.env` is
  git-ignored; `.env.example` only holds placeholders.
- **CORS**: set `CORS_ALLOWED_ORIGINS` to your real frontend origin(s) in production.
- **Database migrations**: Flyway runs automatically on backend startup; review
  `V1__init_schema.sql` before pointing it at a database with existing data.
