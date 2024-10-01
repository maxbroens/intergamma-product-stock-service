package nl.intergamma.product_stock_service.persistence.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.util.UUID

@Entity
@Table(name = "stock", uniqueConstraints = [UniqueConstraint(columnNames = ["productId"])])
data class StockEntity(
    @Id
    val id: UUID = UUID.randomUUID(),
    val productId: UUID,
    var quantity: Int
)