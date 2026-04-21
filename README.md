# Dormio — Mobile App
 
Android application for the Dormio student-housing platform. Dormio helps housemates coordinate chores, split bills, manage a shared schedule, submit maintenance requests, and stay in sync through push notifications.
 
> **Status:** v1.0.0 — initial release.
 
## Features
 
- **Authentication** — Login & registration with JWT, auto session-expiry handling (401/403 interceptor).
- **Home dashboard** — Quick view of the user's day and household activity.
- **Chores** — Create, assign, update, and mark chores as complete; weekly chores view; FCM reminders.
- **Budget** — Create bills, split across housemates, track your share, and log household expenses.
- **Schedule / Calendar** — Shared calendar with event categories and create-event flow.
- **Maintenance requests** — Submit, list, filter, update, and delete maintenance issues; FCM notifications.
- **Push notifications** — Firebase Cloud Messaging with an in-app notification history screen.
- **Profile management** — Update account details.
 
### Prerequisites
- **Android Studio** Hedgehog or newer
- **JDK 17**
- **Android SDK** with the platform level used in `app/build.gradle.kts`
- A running instance of the **Dormio backend API**
- A **Firebase project** with `google-services.json` placed in `app/`

### Clone & run
```bash
git clone https://github.com/bryanyabut/T12-Dormio-Mobile-App.git
cd T12-Dormio-Mobile-App
```

## Demo accounts

- **bob.smith@example.com / pass456**
- **john.doe@example.com / pass123**
- **admin@dormio.com / adminpass**

These accounts are pre-populated with sample data to showcase features like chores, bills, and schedules.
Also contain address for roommate related functionality which may not be available when registering a new user account.
Creation of user and login with user account still works but with limited functionality.

Use the admin account to test admin-only features in maintenance requests

## Known limitations / demo notes

- **API endpoint**: The app is configured for local development using `http://10.0.2.2:3000/api/v1/`, which is reachable only from an Android emulator. For production deployment, update `BASE_URL` in `utils/Constants.kt` to a public HTTPS endpoint and remove `usesCleartextTraffic` from `AndroidManifest.xml`.
- **Test credentials**: `LoginTestActivity` contains dummy credentials and is not part of the app flow; it can be ignored.
