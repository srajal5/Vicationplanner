# Vicationplanner — Full Project README

Last updated: 2025-11-11

## Overview

Vicationplanner is a full-stack vacation planning web application. It includes a Spring Boot backend (Java) exposing trip planning APIs and a React frontend (Create React App) providing the UI for planning trips, viewing generated trip plans, booking a trip, and saving trips.

This README documents the end-to-end work performed (diagnosis, fixes, feature wiring, and styling/theme work), the full tech stack, how to run the project locally on Windows, verification steps, and next steps.

## High-level summary (what was done)

- Diagnosed and fixed frontend → backend integration so the Trip Planner UI actually POSTs preferences to the backend `/api/trips/plan` endpoint.
- Verified backend Spring Boot endpoints are working by POSTing sample TripPreferences and getting a TripPlan back.
- Added React Router wiring (BrowserRouter + Routes) so pages are reachable:
  - `/` → Trip Planner
  - `/results/:tripId` → Trip Results
  - `/booking/:tripId` → Booking
  - `/saved` → Saved Trips
- Fixed runtime React error in `TripResults.js` (moved `fetchTrip` function before useEffect to avoid TDZ reference and adjusted effect dependencies).
- Fixed duplicate/garbled code in `Booking.js` and ensured a single default export.
- Implemented a working theme toggle (light/system/dark) by:
  - Enhancing `ThemeContext` to set both `data-theme` and the `dark` class on `<html>`.
  - Adding comprehensive dark-mode CSS (attribute selector `[data-theme="dark"]` and `.dark`) to `src/index.css` and rebuilding Tailwind output.
- Rebuilt Tailwind output to `public/tailwind.css` and confirmed dark styles are present.

## Tech stack

- Backend
  - Java 17+ (Spring Boot)
  - Maven (build tool)
  - REST controllers under `backend/src/main/java/com/vacationplanner/controller`

- Frontend
  - React 18 (Create React App)
  - React Router v6
  - Tailwind CSS v3 (prebuilt to `public/tailwind.css`) — dark mode enabled via class strategy
  - Axios for HTTP requests
  - Lucide icons
  - Custom UI primitives in `src/ui` (Button, Card, Input)

- Build / Tooling
  - Maven for backend (`mvn spring-boot:run`)
  - npm / node for frontend (`npm start`)
  - Tailwind CLI used to compile `src/index.css` → `public/tailwind.css` (`npm run build:tailwind`)

## Repository layout (relevant parts)

```
README-MODERN-ARCHITECTURE.md
README.md (this file)
backend/
  pom.xml
  src/main/java/com/vacationplanner/...
frontend/
  package.json
  public/
    index.html
    tailwind.css (generated)
  src/
    index.js
    index.css
    App.js
    contexts/ThemeContext.js
    components/
      Navbar.js
      ThemeToggle.js
    pages/
      TripPlanner.js
      TripResults.js
      Booking.js
      SavedTrips.js
    ui/ (Button, Card, Input primitives)
```

## Files changed or added during work (important ones)

- `frontend/src/index.js` — wrapped App in BrowserRouter
- `frontend/src/App.js` — added Routes and Route definitions
- `frontend/src/pages/TripPlanner.js` — updated to POST preferences to `/api/trips/plan` and handle loading/error states
- `frontend/src/pages/TripResults.js` — fixed fetch ordering bug and adjusted useEffect dependencies
- `frontend/src/pages/Booking.js` — removed duplicate/garbled code; ensured single export and fixed lint warnings
- `frontend/src/components/ThemeToggle.js` — toggle UI that calls setTheme('light'|'dark'|'system')
- `frontend/src/contexts/ThemeContext.js` — updated to write `data-theme` and toggle `document.documentElement.classList` for dark mode to be compatible with Tailwind's `class` strategy
- `frontend/src/index.css` — added many UI styles plus `[data-theme="dark"]` selectors and `.dark` compatibility rules; kept Tailwind @layer utilities and custom components
- `frontend/tailwind.config.js` — ensured `darkMode: ['class']` is enabled and `content` points at src files
- `frontend/public/tailwind.css` — generated output (via Tailwind CLI)

Backend: edits were primarily diagnostic and no breaking API changes were introduced; backend endpoints were tested successfully.

## How to run locally (Windows PowerShell)

Prereqs
- Java 17+ installed and available on PATH
- Maven 3.6+ (or use the included wrapper)
- Node.js 16+ and npm

1) Start the backend (Spring Boot)

Open PowerShell and run:

```powershell
cd C:\Users\ASUS\Vicationplanner\backend
mvn spring-boot:run
# or use the wrapper if present: .\mvnw spring-boot:run
```

Expected: Backend binds to port 8080. Verify with a quick GET (PowerShell):

```powershell
Invoke-RestMethod -Method Get -Uri http://localhost:8080/api/trips
```

2) Start the frontend (development)

Open a second PowerShell terminal and run:

```powershell
cd C:\Users\ASUS\Vicationplanner\frontend
npm install
# Build Tailwind once (the project uses a prestart builder that also runs this, but run explicitly to regenerate CSS after style edits):
npm run build:tailwind
# Start the CRA dev server (it may prompt to use another port if 3000 is busy; in our session it used 3001)
npm start
```

Notes on the Tailwind build
- Tailwind is compiled from `src/index.css` to `public/tailwind.css`. If you edit `src/index.css`, re-run `npm run build:tailwind`.

3) Open the app
- Frontend default: `http://localhost:3001` (or 3000 if available)
- Backend default API base: `http://localhost:8080`

CRA proxy
- `frontend/package.json` contains a `proxy` setting pointing to `http://localhost:8080`, allowing relative `/api/*` requests from the frontend to forward to the backend.

## Important commands (Windows PowerShell)

Rebuild Tailwind:

```powershell
cd C:\Users\ASUS\Vicationplanner\frontend
npm run build:tailwind
```

Start backend:

```powershell
cd C:\Users\ASUS\Vicationplanner\backend
mvn spring-boot:run
```

Start frontend:

```powershell
cd C:\Users\ASUS\Vicationplanner\frontend
npm start
```

## API endpoints (summary)

- POST `/api/trips/plan` — accepts TripPreferences JSON and returns a generated TripPlan object (used by TripPlanner page)
- GET `/api/trips` — list of saved/tracked trips (for SavedTrips page)
- GET `/api/trips/{tripId}` — fetch a previously generated trip plan by id (used by TripResults and Booking pages)
- POST `/api/booking/book` — submit booking data (Booking page demo endpoint)

Example POST (PowerShell):

```powershell
$prefs = @{
  startDate = "2025-12-01";
  endDate = "2025-12-08";
  startingPoint = "JFK";
  currency = "USD";
  groupSize = 2
} | ConvertTo-Json

Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/trips/plan -Body $prefs -ContentType 'application/json'
```

You should receive a TripPlan JSON response including itinerary, accommodation, transport, and budget breakdown.

## Theme system / Dark mode details (what I changed)

Problem
- The project originally had a `ThemeContext` that set a `data-theme` attribute on `document.documentElement`, but the stylesheet used `.dark` selectors (or had no selectors that matched the `data-theme` attribute). Result: toggling theme changed the stored value but did not change UI styling.

Fix implemented
- `ThemeContext` now:
  - Reads saved preference from localStorage (default `system`).
  - Resolves system preference when `system` is selected (via `prefers-color-scheme`).
  - Sets `document.documentElement.setAttribute('data-theme', resolvedTheme)`.
  - Also toggles the `dark` class on `document.documentElement` when resolved theme is dark; this works with Tailwind's `darkMode: ['class']` setting.
- CSS updates:
  - `src/index.css` contains both attribute-based selectors `[data-theme="dark"]` for robustness and `.dark` selectors for Tailwind compatibility.
  - Rebuilt Tailwind output (`public/tailwind.css`) so custom dark rules are included in development output.

Outcome
- Clicking the ThemeToggle (Light / Dark / System) now updates the UI appearance immediately, persists the preference in localStorage, and continues to respect `prefers-color-scheme` when `system` is selected.

## What I tested / verification

- Verified backend API responds to POST `/api/trips/plan` with a full TripPlan JSON.
- Confirmed TripPlanner page posts preferences and navigates to `/results/{tripId}` when a plan is returned.
- Confirmed TripResults fetches trip plan from `/api/trips/{tripId}` and displays content (no TDZ/initialization error after moving fetch function above useEffect).
- Confirmed Booking page loads without duplicate export errors and the stepper works (demo booking endpoint used).
- Confirmed theme toggle switches UI between light/dark visually and persists preference across reloads.

## Troubleshooting

- If frontend cannot reach backend:
  - Verify backend is running on port 8080.
  - Confirm `frontend/package.json` proxy setting is `http://localhost:8080`.
  - Check browser Console/Network tab for CORS or proxy errors.

- If Tailwind styles don't update after editing `src/index.css`:
  - Re-run `npm run build:tailwind`.
  - Ensure `tailwind.config.js` `content` globs include all folders where your classes live (src, components, pages).

- If React dev server tries to use a different port (3000 busy): accept the prompt (it will use 3001), or terminate the process running on 3000.

## Next steps and recommendations

- Migrate all remaining MUI-based pages/components to Tailwind/shadcn style primitives for consistent look and smaller bundle.
- Introduce unit/integration tests for frontend components and API contract tests for backend endpoints.
- Add persistent storage for saved trips (DB) instead of in-memory storage in backend (if not already present).
- Add end-to-end tests (Cypress/Playwright) to validate TripPlanner → results → booking flows.

## Contribution notes

- Follow the current repo style: JS (not TypeScript). If TypeScript is desired later, add a careful migration plan and update tooling.

## Who did what (summary timeline)

1. Diagnosed missing TripPlanner POST wiring and added code to `TripPlanner.js` to POST to `/api/trips/plan`.
2. Added routing in `App.js` and wrapped the app with `BrowserRouter` in `index.js`.
3. Fixed `TripResults.js` hook initialization error (moved `fetchTrip` above `useEffect`).
4. Fixed `Booking.js` duplicate export and syntax issues.
5. Implemented robust theme toggle: updated `ThemeContext.js`, added `[data-theme="dark"]` CSS entries and `.dark` fallbacks, and rebuilt Tailwind CSS output.

---

If you want, I can also:

- Add short developer scripts to `package.json` to automate Tailwind rebuild in watch mode.
- Add a small troubleshooting script that checks backend process and port and prints a short status report.

Would you like me to add any of those extras now?
#   V i c a t i o n p l a n n e r  
 