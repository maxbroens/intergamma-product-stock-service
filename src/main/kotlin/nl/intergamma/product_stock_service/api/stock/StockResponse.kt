package nl.intergamma.product_stock_service.api.stock

import java.util.UUID

data class StockResponse(
    val productId: UUID,
    val quantity: Int
)
