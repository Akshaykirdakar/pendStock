package com.pendshop.stockmanager.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.pendshop.stockmanager.data.model.*
import kotlinx.coroutines.tasks.await

/**
 * Single place that talks to Firestore. Keep all collection names here so the
 * rest of the app never hardcodes a Firestore path.
 */
class FirestoreRepository {

    private val db = FirebaseFirestore.getInstance()

    private val brandsRef = db.collection("brands")
    private val productsRef = db.collection("products")
    private val stockRef = db.collection("stock")
    private val stockLogsRef = db.collection("stockLogs")
    private val billsRef = db.collection("bills")
    private val countersRef = db.collection("counters").document("billCounter")

    // ---------- Brands ----------

    suspend fun addBrand(name: String): String {
        val doc = brandsRef.document()
        val brand = Brand(id = doc.id, name = name)
        doc.set(brand).await()
        return doc.id
    }

    suspend fun getBrands(): List<Brand> =
        brandsRef.orderBy("name").get().await().toObjects(Brand::class.java)

    // ---------- Products ----------

    suspend fun addOrUpdateProduct(product: Product): String {
        val docRef = if (product.id.isBlank()) productsRef.document() else productsRef.document(product.id)
        val toSave = product.copy(id = docRef.id, qrCode = docRef.id, updatedAt = System.currentTimeMillis())
        docRef.set(toSave).await()

        // Ensure a stock document exists for this product (default 0 if new)
        val stockDoc = stockRef.document(docRef.id).get().await()
        if (!stockDoc.exists()) {
            stockRef.document(docRef.id).set(Stock(productId = docRef.id)).await()
        }
        return docRef.id
    }

    suspend fun getProducts(): List<Product> =
        productsRef.orderBy("brandName").get().await().toObjects(Product::class.java)

    suspend fun getProductById(productId: String): Product? =
        productsRef.document(productId).get().await().toObject(Product::class.java)

    // ---------- Stock ----------

    suspend fun getStock(productId: String): Stock =
        stockRef.document(productId).get().await().toObject(Stock::class.java) ?: Stock(productId = productId)

    suspend fun getAllStock(): List<Stock> =
        stockRef.get().await().toObjects(Stock::class.java)

    /** Manual stock-in / purchase entry, or a manual adjustment (positive or negative bags). */
    suspend fun adjustStockBags(productId: String, bagsDelta: Int, note: String, type: StockLogType) {
        db.runTransaction { txn ->
            val stockSnap = txn.get(stockRef.document(productId))
            val current = stockSnap.toObject(Stock::class.java) ?: Stock(productId = productId)
            val updated = current.copy(
                bagsRemaining = current.bagsRemaining + bagsDelta,
                updatedAt = System.currentTimeMillis()
            )
            txn.set(stockRef.document(productId), updated)

            val logDoc = stockLogsRef.document()
            txn.set(
                logDoc,
                StockLog(
                    id = logDoc.id,
                    productId = productId,
                    type = type.name,
                    quantity = bagsDelta.toDouble(),
                    note = note
                )
            )
        }.await()
    }

    suspend fun getStockLogs(productId: String): List<StockLog> =
        stockLogsRef.whereEqualTo("productId", productId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get().await().toObjects(StockLog::class.java)

    // ---------- Billing / Checkout ----------

    /**
     * Finalizes a bill: writes the bill + its items, and deducts stock for every
     * line item atomically. If ANY line item can't be fulfilled (not enough stock),
     * the whole transaction fails and nothing is written/deducted.
     *
     * "Open a new bag" logic: if a KG sale needs more loose kg than currently
     * available, one full bag is converted into loose kg automatically before
     * deducting the sold quantity, as long as a bag is available.
     */
    suspend fun checkoutBill(
        customerName: String,
        paymentMode: PaymentMode,
        items: List<BillItem>
    ): Pair<String, Long> {
        val billDocRef = billsRef.document()
        val totalAmount = items.sumOf { it.lineTotal }
        var assignedBillNumber = 0L

        db.runTransaction { txn ->
            // ---- All reads must happen before any writes in a Firestore transaction ----
            val counterSnap = txn.get(countersRef)
            val nextBillNumber = (counterSnap.getLong("lastBillNumber") ?: 0L) + 1

            val stockSnapshots = items.associate { item ->
                item.productId to txn.get(stockRef.document(item.productId))
            }

            // ---- Now perform writes ----
            assignedBillNumber = nextBillNumber
            txn.set(countersRef, mapOf("lastBillNumber" to nextBillNumber))

            for (item in items) {
                val stockDocRef = stockRef.document(item.productId)
                var stock = stockSnapshots[item.productId]?.toObject(Stock::class.java)
                    ?: Stock(productId = item.productId)

                when (SaleType.valueOf(item.saleType)) {
                    SaleType.BAG -> {
                        val bagsNeeded = item.quantityOrWeight.toInt()
                        if (stock.bagsRemaining < bagsNeeded) {
                            throw IllegalStateException("Not enough bag stock for ${item.productName}")
                        }
                        stock = stock.copy(bagsRemaining = stock.bagsRemaining - bagsNeeded)
                    }
                    SaleType.KG -> {
                        val kgNeeded = item.quantityOrWeight
                        if (stock.looseKgRemaining < kgNeeded) {
                            throw IllegalStateException(
                                "Insufficient loose stock for ${item.productName}. " +
                                "Open a bag manually first via Stock screen, then retry checkout."
                            )
                        }
                        stock = stock.copy(looseKgRemaining = stock.looseKgRemaining - kgNeeded)
                    }
                }
                txn.set(stockDocRef, stock.copy(updatedAt = System.currentTimeMillis()))

                val logDoc = stockLogsRef.document()
                txn.set(
                    logDoc,
                    StockLog(
                        id = logDoc.id,
                        productId = item.productId,
                        type = StockLogType.SALE.name,
                        quantity = item.quantityOrWeight,
                        note = "Bill #$nextBillNumber (${billDocRef.id})"
                    )
                )
            }

            val bill = Bill(
                id = billDocRef.id,
                billNumber = nextBillNumber,
                customerName = customerName,
                totalAmount = totalAmount,
                paymentMode = paymentMode.name
            )
            txn.set(billDocRef, bill)

            for (item in items) {
                val itemDoc = billDocRef.collection("billItems").document()
                txn.set(itemDoc, item.copy(id = itemDoc.id))
            }
        }.await()

        return billDocRef.id to assignedBillNumber
    }

    /** Explicit "open a bag" action from the Stock screen: converts 1 bag into loose kg. */
    suspend fun openBag(productId: String, bagWeightKg: Double) {
        db.runTransaction { txn ->
            val stockDocRef = stockRef.document(productId)
            val stock = txn.get(stockDocRef).toObject(Stock::class.java) ?: Stock(productId = productId)
            if (stock.bagsRemaining <= 0) {
                throw IllegalStateException("No full bags left to open")
            }
            val updated = stock.copy(
                bagsRemaining = stock.bagsRemaining - 1,
                looseKgRemaining = stock.looseKgRemaining + bagWeightKg,
                updatedAt = System.currentTimeMillis()
            )
            txn.set(stockDocRef, updated)

            val logDoc = stockLogsRef.document()
            txn.set(
                logDoc,
                StockLog(
                    id = logDoc.id,
                    productId = productId,
                    type = StockLogType.BAG_OPENED.name,
                    quantity = bagWeightKg,
                    note = "1 bag opened into loose kg"
                )
            )
        }.await()
    }

    suspend fun getBillsBetween(startMillis: Long, endMillis: Long): List<Bill> =
        billsRef.whereGreaterThanOrEqualTo("createdAt", startMillis)
            .whereLessThanOrEqualTo("createdAt", endMillis)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get().await().toObjects(Bill::class.java)

    suspend fun getBillItems(billId: String): List<BillItem> =
        billsRef.document(billId).collection("billItems").get().await().toObjects(BillItem::class.java)
}
