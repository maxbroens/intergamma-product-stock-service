package nl.intergamma.product_stock_service.persistence.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "reservation")
data class ReservationEntity(

    @Id
    val id: UUID = UUID.randomUUID(),
    val productId: UUID,
    var quantity: Int,
    val reservedAt: LocalDateTime = LocalDateTime.now()
)