package nl.intergamma.product_stock_service.api.reservation

import java.util.UUID

data class ReservationRequest(
    val productId: UUID,
    val quantity: Int
)