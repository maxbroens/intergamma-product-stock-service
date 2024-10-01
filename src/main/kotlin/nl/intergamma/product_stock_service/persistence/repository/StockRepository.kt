package nl.intergamma.product_stock_service.persistence.repository

import nl.intergamma.product_stock_service.persistence.entity.StockEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional
import java.util.UUID

@Repository
interface StockRepository : JpaRepository<StockEntity, UUID> {
    fun findByProductId(productId: UUID): Optional<StockEntity>
}