# Pend Shop Stock Manager (Android + Firebase)

Native Android app (Kotlin + Jetpack Compose) for a पेंड (cattle feed) shop:
catalogue with per-product QR codes, QR-scan based billing (full bag or by
weight/kg), multi-item cart, live stock tracking, low-stock alerts, and daily
sales reporting. Backend is Firebase (Firestore + Storage + Auth).

## Quick Start

1. Read **`firebase-setup-guide.md`** first — you must create a Firebase
   project and replace `app/google-services.json` before this app will run.
2. Open this folder in Android Studio (Hedgehog/2023.1.1+).
3. Let Gradle sync, plug in a physical Android device (for camera/QR testing),
   and run.

## Project Structure

```
app/src/main/java/com/pendshop/stockmanager/
├── data/
│   ├── model/            # Brand, Product, Stock, StockLog, Bill, BillItem
│   └── repository/       # FirestoreRepository (all DB calls, checkout transaction), CartRepository
├── ui/
│   ├── navigation/       # NavGraph + HomeScreen (auth-gated start destination)
│   ├── screens/
│   │   ├── auth/         # Login (Firebase Auth email/password)
│   │   ├── catalogue/    # Catalogue list, Add/Edit Product (with photo upload), QR Catalogue (share/print)
│   │   ├── scan/         # QR scan (CameraX + ML Kit), Product Detail
│   │   ├── cart/         # Cart + checkout + receipt sharing
│   │   ├── stock/        # Stock dashboard, low-stock alerts, open-bag
│   │   └── reports/      # Today's sales report
│   └── theme/            # Compose theme/colors
├── viewmodel/            # One ViewModel per feature area (incl. AuthViewModel)
├── util/                 # QrCodeGenerator, QrShareHelper, StorageUploader
├── MainActivity.kt
└── PendShopApp.kt        # Application class, initializes Firebase
```

## Building the APK via GitHub Actions (no Android Studio needed)

This repo includes `.github/workflows/build-apk.yml`, which builds a debug APK
automatically every time you push to `main`, and can also be run manually.

1. Push this repo to GitHub (see the steps you already have for that).
2. Go to your repo on GitHub → the **Actions** tab.
3. You should see a workflow run start automatically after your push (or click
   **Build Debug APK** in the left sidebar → **Run workflow** to trigger it manually).
4. Click into the run once it finishes (green checkmark = success).
5. Scroll to the **Artifacts** section at the bottom of the run page → download
   **app-debug-apk** — this is a zip containing your `.apk` file.
6. Transfer that `.apk` to an Android phone (email it to yourself, upload to
   Google Drive, etc.) and install it (you'll need to allow "install from
   unknown sources" the first time, since it's not from the Play Store).

**Important:** the build will succeed even with the placeholder
`app/google-services.json`, but the app won't actually be able to log in or
read/write data until you replace it with your real Firebase config — see
`firebase-setup-guide.md` for exactly how to do that without needing Android
Studio.

## Docs

- `docs/Requirements.md` — full requirements spec (SRS) for this project
- `firebase-setup-guide.md` — step-by-step Firebase project setup, plus what's built vs. left
- `firestore.rules` / `storage.rules` — security rules to paste into Firebase console

## Status

Functional end-to-end for a single shop owner/staff account: login, catalogue with
photo + QR, QR-scan billing with cart and price override, sequential bill numbers,
receipt sharing, stock tracking with low-stock alerts, and a daily report. See the
"Left to build" list at the bottom of `firebase-setup-guide.md` for the remaining
polish items (dedicated printer SDK, multi-staff roles, credit ledger, etc.).
