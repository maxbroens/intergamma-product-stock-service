package nl.intergamma.product_stock_service.api.reservation

import java.time.LocalDateTime
import java.util.UUID

data class ReservationResponse(
    val id: UUID,
    val productId: UUID,
    val quantity: Int,
    val reservedAt: LocalDateTime
)