# Firebase Setup Guide — Pend Shop Stock Manager

Follow these steps in order. This app uses **Firestore** (database), **Firebase Storage**
(product photos), and **Firebase Authentication** (login).

---

## 1. Create the Firebase Project

1. Go to https://console.firebase.google.com
2. Click **Add project** → name it e.g. `pend-shop-stock-manager` → follow the wizard
   (Google Analytics is optional, you can skip it).
3. Once created, you land on the project dashboard.

---

## 2. Register the Android App

1. In the project dashboard, click the **Android icon** to add an Android app.
2. **Android package name**: enter exactly `com.pendshop.stockmanager`
   (must match `applicationId` in `app/build.gradle.kts` — don't change one without the other).
3. App nickname: anything, e.g. "Pend Shop".
4. SHA-1 (optional for now): only required later if you enable Google Sign-In. Skip for
   basic email/password auth.
5. Click **Register app**.

---

## 3. Download `google-services.json`

1. Firebase will offer a `google-services.json` file to download.
2. **Replace** the placeholder file at `app/google-services.json` in this project with
   the real one you just downloaded (delete the placeholder, put the real file in the
   exact same location: `app/google-services.json`).
3. This file contains your project's API keys and IDs — the app will not connect to
   Firebase without the real version.

> ⚠️ If your Git repo is **public**, don't commit the real `google-services.json` with
> production keys — the `.gitignore` in this project already excludes it by default.
> If your repo is private, you can remove that line from `.gitignore` to track it.

---

## 4. Enable Firestore Database

1. In the Firebase console sidebar: **Build → Firestore Database**.
2. Click **Create database**.
3. Choose **Production mode** (we provide our own rules below) or Test mode if you want
   open access for the first few days of development — just remember to lock it down.
4. Pick a Firestore **location** close to you (e.g. `asia-south1` for India) — this
   cannot be changed later.
5. Once created, go to the **Rules** tab and paste in the contents of `firestore.rules`
   from this project's root folder. Click **Publish**.

---

## 5. Enable Firebase Storage (for product photos)

1. Sidebar: **Build → Storage**.
2. Click **Get started**, accept the default bucket location (ideally same region as
   Firestore).
3. Go to the **Rules** tab and paste in the contents of `storage.rules` from this
   project. Click **Publish**.

---

## 6. Enable Authentication

1. Sidebar: **Build → Authentication**.
2. Click **Get started**.
3. Under **Sign-in method**, enable **Email/Password** (simplest for a single
   owner + a couple of staff accounts).
4. Under the **Users** tab, manually add the shop owner's login (email + password) —
   this is the account used to sign in on the counter device.

> The current app code doesn't yet include a login screen — Firestore/Storage rules
> above require `request.auth != null`, so you'll need to add a simple sign-in step
> (Firebase Auth's `signInWithEmailAndPassword`) before the app can read/write data.
> This is a natural next task — flag it if you want it built out.

---

## 7. Build the Project

1. Clone this repo, open it in **Android Studio** (Hedgehog / 2023.1.1 or newer
   recommended).
2. Let Gradle sync — it will download all dependencies listed in
   `app/build.gradle.kts` (Firebase BoM, CameraX, ML Kit, Compose, ZXing, Coil).
3. Confirm `app/google-services.json` is the **real** file from Step 3, not the
   placeholder.
4. Run on a physical device (recommended, for camera/QR testing) or an emulator with
   a working camera feed.

---

## 8. First-Time Data Setup

There's no bulk-import tool yet, so the very first brands/products need to be entered
by hand through the app:

1. Open the app → **Catalogue** → **+** → add your first brand name → add a product
   under it (name, bag weight, prices) → Save.
2. This automatically creates a matching `stock` document at 0 bags / 0 kg.
3. Go to **Stock Dashboard** → tap **+10 bags (purchase)** (or wire up a proper
   "Add Stock" dialog) to record your opening stock.
4. Print the catalogue: generate each product's QR using `QrCodeGenerator.generate(productId)`
   — a dedicated "Print Catalogue" export screen isn't built yet; for now you can view/
   share one QR bitmap at a time from the Add/Edit Product screen (hook this up next).

---

## 9. What's Already Built vs. What's Left

**Built (functional skeleton):**
- Data models, Firestore repository, atomic checkout transaction with stock deduction
- Catalogue: add brand, add product (auto-generates QR code string = product ID)
- QR scan via CameraX + ML Kit → Product Detail screen
- Product Detail: full-bag or by-kg sale, **editable price with override flag**
- Cart: multi-item, remove line, checkout with customer name + payment mode
- Stock Dashboard: low-stock alert banner, manual "+10 bags" and "open 1 bag" actions
- Today's Reports screen (total sales + bill list)

**Left to build (natural next steps):**
- Login screen (Firebase Auth email/password)
- Product photo picker + Storage upload (currently a `TODO` in `AddEditProductScreen.kt`)
- Printable/shareable QR catalogue sheet (grid of QR + photo + name)
- Proper "Add Stock" dialog (currently hardcoded to +10 bags for quick testing)
- Bill receipt printing (Bluetooth thermal printer SDK integration)
- Sequential human-friendly bill numbers (currently uses a timestamp — fine for
  uniqueness, but swap in a Firestore counter document for "Bill #1, #2, #3…")
- Multi-staff roles/permissions (admin vs. staff), if confirmed needed
- Customer credit/khata ledger, if confirmed needed
