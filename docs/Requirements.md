# Software Requirements Specification
## Stock Maintenance & Billing System — Cattle Feed (पेंड) Shop

---

## 1. Project Overview

The client operates a पेंड (cattle feed) shop selling bagged feed products of multiple brands and varieties. He wants a system where each bag type has a printed QR code (via a catalogue). Scanning the QR retrieves product details, photo, and price, and allows sale either as a full bag or by weight (per kg). The system must also track inventory in real time and alert on low stock.

---

## 2. Scope

- Catalogue creation and management (brands, bag types, prices, photos, QR codes)
- QR-based product lookup at point of sale
- Multi-item cart / invoice billing (full bag and/or partial by-weight sales in one bill)
- Real-time inventory tracking (bag-level and loose-kg level)
- Stock alerts and daily sales reporting
- Basic reporting/dashboard for the shop owner

Out of scope (unless confirmed later): online sales, multi-branch sync, accounting/GST filing integration, supplier/purchase management (see Section 8 — recommend including this too).

---

## 3. Users

| Role | Description |
|---|---|
| Shop Owner / Admin | Manages catalogue, brands, prices, views reports, stock alerts |
| Counter Staff (if any) | Scans QR codes, builds bill, completes sale |

*(To confirm: single-user or multi-staff with logins — see open questions)*

---

## 4. Functional Requirements

### 4.1 Catalogue Management
- FR1.1: Admin can add/edit/delete **Brands** (e.g., Godrej, Kargil, local brands).
- FR1.2: Admin can add/edit/delete **Bag Types/Products** under a brand — includes:
  - Product name / variety
  - Bag weight (e.g., 25kg, 50kg)
  - Product photo (upload from gallery/camera)
  - Full-bag price
  - Per-kg price (independent field, not auto-calculated)
  - Reorder/low-stock threshold
- FR1.3: System auto-generates a unique QR code per product (product-level, not per physical unit).
- FR1.4: Admin can print/export QR codes as a catalogue sheet (grid layout with photo + name under each QR, for pasting/printing).

### 4.2 QR Scan & Product Lookup
- FR2.1: Scanning a QR opens product detail screen showing: photo, brand, variety, bag weight, full-bag price, per-kg price, current stock (bags + loose kg).
- FR2.2: From this screen, user selects sale type: **Full Bag** or **By Weight (kg)**.
- FR2.3: Scanned item is added as a line to an active **Cart/Bill** (not sold immediately).

### 4.3 Cart & Billing
- FR3.1: A single bill can contain multiple line items across different brands/products.
- FR3.2: Each line item stores: product, sale type, quantity (bags) or weight (kg), rate, line total.
- FR3.3: User can edit quantity/weight or remove a line before checkout.
- FR3.3a: The rate (per-bag or per-kg price) is editable at the time of sale — staff can override the catalogue price for a given line item (e.g., for bargaining, wholesale/bulk discount, or long-time customers). The catalogue price remains the default that auto-fills when the item is scanned; only the current bill's line is affected, not the master price.
- FR3.3b: System should log if a sale price was overridden from the catalogue price (for the owner's own tracking/audit — e.g., a small flag or note showing "price edited" in reports), so he can see how often and by how much staff are discounting.
- FR3.4: Running total updates live as items are added.
- FR3.5: On checkout, bill is finalized: bill number, date/time, total amount, payment mode (cash/UPI/credit — confirm), optional customer name/mobile.
- FR3.6: Stock is deducted for all line items atomically only on bill finalization (not on scan).
- FR3.7: Option to print/share bill (PDF/WhatsApp — confirm need).

### 4.4 Inventory Management
- FR4.1: Each product tracks two stock units: **full bags remaining** and **loose kg remaining** (from opened bags).
- FR4.2: When loose kg stock is insufficient for a by-weight sale, system prompts to "open a new bag," converting 1 bag → full bag-weight of loose kg.
- FR4.3: Admin can manually adjust stock (new purchase/stock-in entry, damage/loss write-off).
- FR4.4: Stock history log per product (in/out/adjustment with date).

### 4.5 Alerts & Reporting
- FR5.1: Low-stock alert per product when bags remaining fall below configured threshold.
- FR5.2: Daily sales summary — total revenue, items sold, bags vs. kg breakdown.
- FR5.3: Date-range sales report (weekly/monthly), filterable by brand/product.
- FR5.4: Current stock dashboard — all products with bags + loose kg at a glance.

### 4.6 User Management *(pending confirmation — see open questions)*
- FR6.1: Login for admin; optional additional staff logins with restricted access (e.g., can bill, cannot edit prices).

---

## 5. Non-Functional Requirements

- NFR1: App should work with intermittent/no internet at the counter (local-first data, sync when online, if cloud backup is desired).
- NFR2: QR scan response time under 1–2 seconds.
- NFR3: Data backup — daily automatic backup of stock/sales data.
- NFR4: Simple, minimal-taps UI suitable for non-technical shop staff.
- NFR5: Support for Marathi/Hindi labels in UI (product names, brand names) alongside English, since client operates in Marathi context.

---

## 6. Core Data Entities

- **Brand**: id, name
- **Product**: id, brand_id, name/variety, bag_weight_kg, photo, full_bag_price, per_kg_price, low_stock_threshold, qr_code
- **Stock**: product_id, bags_remaining, loose_kg_remaining
- **StockLog**: product_id, type (purchase/sale/adjustment/bag-opened), quantity, date
- **Bill**: id, date_time, customer_name (optional), total_amount, payment_mode
- **BillItem**: bill_id, product_id, sale_type (bag/kg), quantity_or_weight, rate, line_total, is_price_overridden (flag vs. catalogue price)

---

## 7. Technology Decision

- **Platform**: Android app (native) — chosen over an HTML/PWA web app for reliable offline use, faster native QR scanning (ML Kit), and easier Bluetooth thermal printer integration for bills.
- **Backend/DB**: Firebase — Firestore as the primary database, with offline persistence enabled so the app keeps working at the counter even without internet, syncing automatically once back online.
- **QR generation**: standard QR library (e.g., ZXing) — generate at product creation, print via catalogue export.
- **QR scanning**: device camera via ML Kit / ZXing scanner.
- **Future option**: a lightweight web dashboard (for the owner to check reports from any device) can be added later on top of the same Firebase backend without needing a separate system.

---

## 8. Open Questions for Client (recommend confirming before development)

1. Does per-kg price ever differ from (full-bag price ÷ bag weight), or is it always proportional? This affects whether it's a stored field or calculated.
2. Single user (just him) or multiple staff with logins/permissions?
3. Is a printed/shared bill/receipt required, or is tracking purely internal?
4. Payment modes to track — cash only, or cash/UPI/credit (udhaar) separately? Many small shops need a "credit/khata" (customer ledger) feature — worth asking.
5. Does he want **purchase/supplier tracking** too (stock coming in from suppliers), or will stock-in be a manual quantity adjustment only?
6. Device availability — does he have an Android phone/tablet at the counter, or is a separate barcode/QR scanner device involved?
7. Any need for multi-branch/multi-shop support in the future?
8. Since selling price can be overridden per sale — should this be open to any staff, or restricted to admin only (with staff needing a PIN/approval to discount)? Also, should there be a minimum price floor to prevent accidental underselling?

---

## 9. Firebase Firestore Structure (Proposed)

```
brands (collection)
  {brandId} (document)
    name: "Godrej"
    createdAt: timestamp

products (collection)
  {productId} (document)
    brandId: ref -> brands/{brandId}
    name: "Cattle Feed Premium"
    bagWeightKg: 50
    photoUrl: "storage path/URL"
    fullBagPrice: 1200
    perKgPrice: 26
    lowStockThreshold: 5
    qrCode: "unique code string / encoded productId"
    createdAt / updatedAt: timestamp

stock (collection)  -- one doc per product, 1:1
  {productId} (document)
    bagsRemaining: 40
    looseKgRemaining: 12
    updatedAt: timestamp

stockLogs (collection)
  {logId} (document)
    productId: ref -> products/{productId}
    type: "purchase" | "sale" | "adjustment" | "bag-opened"
    quantity: number
    note: string (optional)
    createdAt: timestamp

bills (collection)
  {billId} (document)
    billNumber: 1001
    customerName: "" (optional)
    totalAmount: 3450
    paymentMode: "cash" | "upi" | "credit"
    createdAt: timestamp
    createdBy: staffId (if multi-user later)

    billItems (subcollection under each bill)
      {itemId} (document)
        productId: ref -> products/{productId}
        saleType: "bag" | "kg"
        quantityOrWeight: number
        rate: number
        lineTotal: number
        isPriceOverridden: boolean

staff (collection) -- only needed if multi-user login is confirmed
  {staffId} (document)
    name: string
    role: "admin" | "staff"
```

**Notes:**
- `stock` is kept as its own collection (not nested in `products`) so real-time stock listeners are lightweight and don't reload full product/photo data.
- `billItems` as a subcollection under each `bills` document keeps bills self-contained and easy to query/export per day.
- Photos go in **Firebase Storage**, with only the URL stored in Firestore.
- For the daily/date-range sales reports (FR5.2–5.3), query `bills` by `createdAt` range, and aggregate from `billItems` via a Cloud Function or client-side aggregation if volume stays small.

---

## 10. Suggested Phased Delivery

- **Phase 1**: Catalogue management + QR generation/printing + product lookup by scan
- **Phase 2**: Cart/billing with multi-item support + stock deduction
- **Phase 3**: Stock alerts, dashboards, daily/periodic reports
- **Phase 4** (optional): Customer credit ledger, supplier/purchase tracking, multi-user roles, cloud backup
