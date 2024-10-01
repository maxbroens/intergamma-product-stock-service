package nl.intergamma.product_stock_service.persistence.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.math.BigDecimal
import java.util.UUID

@Entity
@Table(name = "product", uniqueConstraints = [UniqueConstraint(columnNames = ["ean", "productName"])])
data class ProductEntity(
    @Id
    val id: UUID = UUID.randomUUID(),
    var ean: String,
    var productName: String,
    var description: String,
    var price: BigDecimal
)