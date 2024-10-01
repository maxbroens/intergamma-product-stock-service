package nl.intergamma.product_stock_service.persistence.repository

import nl.intergamma.product_stock_service.persistence.entity.ReservationEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.UUID

@Repository
interface ReservationRepository : JpaRepository<ReservationEntity, UUID> {
    fun findAllByProductIdAndReservedAtGreaterThanEqual(productId: UUID, reservedAt: LocalDateTime): List<ReservationEntity>
}