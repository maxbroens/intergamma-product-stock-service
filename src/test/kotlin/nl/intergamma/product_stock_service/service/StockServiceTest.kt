package nl.intergamma.product_stock_service.service

import nl.intergamma.product_stock_service.api.product.ProductResponse
import nl.intergamma.product_stock_service.api.stock.StockRequest
import nl.intergamma.product_stock_service.exception.StockAlreadyExistsException
import nl.intergamma.product_stock_service.exception.StockNotFoundException
import nl.intergamma.product_stock_service.persistence.entity.StockEntity
import nl.intergamma.product_stock_service.persistence.repository.StockRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.math.BigDecimal
import java.util.Optional
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class StockServiceTest {

    @Mock
    lateinit var stockRepository: StockRepository

    @Mock
    lateinit var productService: ProductService

    @InjectMocks
    lateinit var stockService: StockService

    @Test
    fun `getStockOfProduct should return stock response`() {
        val productId = UUID.randomUUID()
        val stockEntity = StockEntity(productId = productId, quantity = 10)
        val product = ProductResponse(productId, "EAN", "Product Name", "Description", BigDecimal(100.0))

        whenever(productService.getProductById(productId)).thenReturn(product)
        whenever(stockRepository.findByProductId(productId)).thenReturn(Optional.of(stockEntity))

        val response = stockService.getStockOfProduct(productId)

        assertEquals(productId, response.productId)
        assertEquals(10, response.quantity)
    }

    @Test
    fun `addStock should throw StockAlreadyExistsException if stock already exists`() {
        val productId = UUID.randomUUID()
        val stockRequest = StockRequest(productId, 10)
        val stockEntity = StockEntity(productId = productId, quantity = 10)
        val product = ProductResponse(productId, "EAN", "Product Name", "Description", BigDecimal(100.0))

        whenever(productService.getProductById(productId)).thenReturn(product)
        whenever(stockRepository.findByProductId(productId)).thenReturn(Optional.of(stockEntity))

        assertThrows(StockAlreadyExistsException::class.java) {
            stockService.addStock(stockRequest)
        }
    }

    @Test
    fun `addStock should save and return stock response`() {
        val productId = UUID.randomUUID()
        val stockRequest = StockRequest(productId, 10)
        val product = ProductResponse(productId, "Product Name", "Description", "EAN", BigDecimal(100.0))
        val stockEntity = StockEntity(productId = productId, quantity = 10)

        whenever(productService.getProductById(productId)).thenReturn(product)
        whenever(stockRepository.findByProductId(productId)).thenReturn(Optional.empty())
        whenever(stockRepository.save(any<StockEntity>())).thenReturn(stockEntity)

        val response = stockService.addStock(stockRequest)

        assertEquals(productId, response.productId)
        assertEquals(10, response.quantity)
    }

    @Test
    fun `updateStock should update and return stock response`() {
        val productId = UUID.randomUUID()
        val stockRequest = StockRequest(productId, 20)
        val stockEntity = StockEntity(productId = productId, quantity = 10)
        val product = ProductResponse(productId, "EAN", "Product Name", "Description", BigDecimal(100.0))

        whenever(productService.getProductById(productId)).thenReturn(product)
        whenever(stockRepository.findByProductId(productId)).thenReturn(Optional.of(stockEntity))
        whenever(stockRepository.save(any<StockEntity>())).thenReturn(stockEntity.apply { quantity = 20 })

        val response = stockService.updateStock(productId, stockRequest)

        assertEquals(productId, response.productId)
        assertEquals(20, response.quantity)
    }

    @Test
    fun `deleteStock should delete stock`() {
        val productId = UUID.randomUUID()
        val stockEntity = StockEntity(productId = productId, quantity = 10)
        val product = ProductResponse(productId, "EAN", "Product Name", "Description", BigDecimal(100.0))

        whenever(productService.getProductById(productId)).thenReturn(product)
        whenever(stockRepository.findByProductId(productId)).thenReturn(Optional.of(stockEntity))

        stockService.deleteStock(productId)

        verify(stockRepository, times(1)).delete(stockEntity)
    }

    @Test
    fun `getStock should throw StockNotFoundException if stock not found`() {
        val productId = UUID.randomUUID()
        val product = ProductResponse(productId, "EAN", "Product Name", "Description", BigDecimal(100.0))

        whenever(productService.getProductById(productId)).thenReturn(product)
        whenever(stockRepository.findByProductId(productId)).thenReturn(Optional.empty())

        assertThrows(StockNotFoundException::class.java) {
            stockService.getStockOfProduct(productId)
        }
    }
}