package nl.intergamma.product_stock_service

import jakarta.annotation.PostConstruct
import nl.intergamma.product_stock_service.persistence.entity.ProductEntity
import nl.intergamma.product_stock_service.persistence.entity.StockEntity
import nl.intergamma.product_stock_service.persistence.repository.ProductRepository
import nl.intergamma.product_stock_service.persistence.repository.StockRepository
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class TestDataInitializer(
    val stockRepository: StockRepository,
    val productRepository: ProductRepository
) {

    @PostConstruct
    fun init() {

        val productA = ProductEntity(
            ean = "0799439112766",
            productName = "Product A",
            description = "This is a description of product A",
            price = BigDecimal("9.99")
        )

        val productB = ProductEntity(
            ean = "0799234567653",
            productName = "Product B",
            description = "This is a description of product B",
            price = BigDecimal("49.99")
        )

        val productC = ProductEntity(
            ean = "0799876352847",
            productName = "Product C",
            description = "This is a description of product C",
            price = BigDecimal("89.99")
        )

        val savedProducts = productRepository.saveAll(listOf(productA, productB, productC))

        val productAstock = StockEntity(productId = savedProducts[0].id, quantity = 10)
        val productBstock = StockEntity(productId = savedProducts[1].id, quantity = 5)
        val productCstock = StockEntity(productId = savedProducts[2].id, quantity = 1)

        stockRepository.saveAll(listOf(productAstock, productBstock, productCstock))
    }
}