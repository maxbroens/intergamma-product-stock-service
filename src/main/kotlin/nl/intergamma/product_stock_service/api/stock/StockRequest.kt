package nl.intergamma.product_stock_service.api.stock

import java.util.UUID

data class StockRequest(
    val productId: UUID,
    val quantity: Int
)
