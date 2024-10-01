package nl.intergamma.product_stock_service.service

import nl.intergamma.product_stock_service.exception.ProductNotFoundException
import nl.intergamma.product_stock_service.persistence.entity.ProductEntity
import nl.intergamma.product_stock_service.persistence.repository.ProductRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import java.math.BigDecimal
import java.util.Optional
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class ProductServiceTest {

    @Mock
    lateinit var productRepository: ProductRepository

    @InjectMocks
    lateinit var productService: ProductService

    @Test
    fun `getProducts should return list of product responses`() {
        val productId = UUID.randomUUID()
        val productEntity = ProductEntity(
            id = productId,
            ean = "EAN",
            productName = "Product Name",
            description = "Description",
            price = BigDecimal(100.0)
        )
        val productEntities = listOf(productEntity)

        whenever(productRepository.findAll()).thenReturn(productEntities)

        val products = productService.getProducts()

        assertEquals(1, products.size)
        assertEquals(productId, products[0].productId)
        assertEquals("EAN", products[0].ean)
        assertEquals("Product Name", products[0].productName)
        assertEquals("Description", products[0].description)
        assertEquals(BigDecimal(100.0), products[0].price)
    }

    @Test
    fun `getProductById should return product response`() {
        val productId = UUID.randomUUID()
        val productEntity = ProductEntity(
            id = productId,
            ean = "EAN",
            productName = "Product Name",
            description = "Description",
            price = BigDecimal(100.0)
        )

        whenever(productRepository.findById(productId)).thenReturn(Optional.of(productEntity))

        val product = productService.getProductById(productId)

        assertEquals(productId, product.productId)
        assertEquals("EAN", product.ean)
        assertEquals("Product Name", product.productName)
        assertEquals("Description", product.description)
        assertEquals(BigDecimal(100.0), product.price)
    }

    @Test
    fun `getProductById should throw ProductNotFoundException if product not found`() {
        val productId = UUID.randomUUID()

        whenever(productRepository.findById(productId)).thenReturn(Optional.empty())

        assertThrows(ProductNotFoundException::class.java) {
            productService.getProductById(productId)
        }
    }
}