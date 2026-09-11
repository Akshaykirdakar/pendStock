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
│   └── repository/       # FirestoreRepository (all DB calls), CartRepository (in-memory cart)
├── ui/
│   ├── navigation/       # NavGraph + HomeScreen
│   ├── screens/
│   │   ├── catalogue/    # Catalogue list, Add/Edit Product
│   │   ├── scan/         # QR scan (CameraX + ML Kit), Product Detail
│   │   ├── cart/         # Cart + checkout
│   │   ├── stock/        # Stock dashboard, low-stock alerts, open-bag
│   │   └── reports/      # Today's sales report
│   └── theme/            # Compose theme/colors
├── viewmodel/            # One ViewModel per feature area
├── util/QrCodeGenerator.kt
├── MainActivity.kt
└── PendShopApp.kt        # Application class, initializes Firebase
```

## Docs

- `docs/Requirements.md` — full requirements spec (SRS) for this project
- `firebase-setup-guide.md` — step-by-step Firebase project setup
- `firestore.rules` / `storage.rules` — security rules to paste into Firebase console

## Status

This is a working **functional skeleton**, not a finished production app. See
the "What's Left" section at the bottom of `firebase-setup-guide.md` for the
prioritized list of remaining work (login screen, photo upload, printable
catalogue, receipt printing, etc.).
