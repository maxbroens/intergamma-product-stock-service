package nl.intergamma.product_stock_service.api.product

import java.math.BigDecimal
import java.util.UUID

data class ProductResponse(
    val productId: UUID,
    val ean: String,
    val productName: String,
    val description: String,
    val price: BigDecimal
)

