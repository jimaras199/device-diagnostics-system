# Device Diagnostics System

End-to-end system for collecting and viewing device telemetry and diagnostic events.

## Tech Stack
- Backend: ASP.NET Core Web API + EF Core + SQLite
- Auth: JWT (register/login)
- Client: Android (Kotlin, Jetpack Compose, Retrofit/OkHttp)
- Docs: Swagger/OpenAPI

## Features (current)
- JWT authentication with protected endpoints
- EF Core migrations + auto migrate on startup
- Devices dashboard endpoint (latest metrics per device)
- Telemetry ingestion + querying
- Android client:
  - Login/Register (toggle)
  - Token persistence (DataStore)
  - Auth-gated navigation
  - Auto logout on HTTP 401 + manual logout

## Repository Structure
- `/backend` – REST API and data persistence
- `/android` – Android client application

## Quickstart

### Backend
- Run from Visual Studio (DeviceDiagnostics.Api)
- Swagger UI: `http://localhost:5275/swagger`
- SQLite DB file: `backend/DeviceDiagnostics.Api/data/device-diagnostics.db`

### Android
- Set `BASE_URL` in `ApiClient.kt` to your backend host (LAN IP if using a physical device)
- Run the app on device/emulator

## API (high-level)
- `POST /auth/register`
- `POST /auth/login`
- `GET /dashboard/devices`
- `GET /devices`, `POST /devices`, `GET /devices/{id}`
- `POST /devices/{id}/telemetry`
- `GET /devices/{id}/telemetry?fromUtc=&toUtc=&metric=`

## Next
- Add `EventsController` (POST/GET device events)
- Show telemetry + events in Device Details screen
