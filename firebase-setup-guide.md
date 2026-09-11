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
   the real one you just downloaded.

### If you're not using Android Studio/local git — replace it directly on GitHub:

1. Go to your repo on GitHub in the browser.
2. Navigate into the `app` folder, click on `google-services.json`.
3. Click the **pencil (Edit)** icon in the top-right of the file view.
4. Delete all the placeholder content, then open your downloaded
   `google-services.json` in any text editor (Notepad, VS Code, etc.), copy its
   entire contents, and paste it into the GitHub editor box.
5. Scroll down, add a commit message like "Add real Firebase config", and click
   **Commit changes** directly to the `main` branch.
6. This will automatically trigger a new GitHub Actions build (per the workflow
   in this repo) using your real Firebase project.

> ⚠️ If your repo is **public**, anyone can see this file's contents once
> committed this way (API keys included). For a personal/testing project this
> is usually fine — Firebase API keys aren't secret credentials in the way a
> password is, and your actual data stays protected by the Firestore/Storage
> **rules** you set up in Steps 4–5 below, not by hiding this file. If you want
> extra caution anyway, make the GitHub repo **private** (Settings → General →
> Danger Zone → Change visibility).

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

> The app now includes a Login screen — sign in with the email/password account you
> create in Step 6's **Users** tab. Only signed-in users can read/write Firestore
> and Storage, per the rules above.

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

**Built (functional):**
- Data models, Firestore repository, atomic checkout transaction with stock deduction
- **Firebase Auth login screen** (email/password) — app now requires sign-in before showing any data, matching the Firestore/Storage rules
- Catalogue: add brand, add product (auto-generates QR code string = product ID)
- **Product photo picker + Firebase Storage upload**, wired into Add Product
- **QR Catalogue screen**: view every product's QR with photo/price, and share/print each one (via Android's share sheet — works with WhatsApp, Drive, or any installed printer app)
- QR scan via CameraX + ML Kit → Product Detail screen
- Product Detail: full-bag or by-kg sale, **editable price with override flag**
- Cart: multi-item, remove line, checkout with customer name + payment mode
- **Sequential human-friendly bill numbers** (via a Firestore counter document, incremented atomically inside the checkout transaction)
- **Receipt sharing** after checkout — plain-text receipt shareable via WhatsApp/SMS/any print app
- Stock Dashboard: low-stock alert banner, manual "+10 bags" and "open 1 bag" actions
- Today's Reports screen (total sales + bill list)

**Left to build (natural next steps):**
- Dedicated Bluetooth thermal-printer SDK integration for hardware receipt printers (current receipt sharing uses Android's generic share sheet, which works with most printer apps but isn't a direct ESC/POS print)
- Proper "Add Stock" dialog with a quantity input (currently hardcoded to +10 bags for quick testing)
- Multi-staff roles/permissions (admin vs. staff) — currently any signed-in Firebase Auth user has full access
- Customer credit/khata ledger, if confirmed needed
- Automatic "open a bag" during a KG sale when loose stock is insufficient (currently requires the staff to manually open a bag first via the Stock screen, then retry checkout)
