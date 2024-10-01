package nl.intergamma.product_stock_service.service

import nl.intergamma.product_stock_service.api.stock.StockRequest
import nl.intergamma.product_stock_service.api.stock.StockResponse
import nl.intergamma.product_stock_service.exception.StockAlreadyExistsException
import nl.intergamma.product_stock_service.exception.StockNotFoundException
import nl.intergamma.product_stock_service.persistence.entity.StockEntity
import nl.intergamma.product_stock_service.persistence.repository.StockRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class StockService(
    val stockRepository: StockRepository,
    val productService: ProductService
) {

    @Transactional(readOnly = true)
    fun getStockOfProduct(productId: UUID): StockResponse {
        val stock = getStock(productId)
        return StockResponse(stock.productId, stock.quantity)
    }

    @Transactional
    fun addStock(stockRequest: StockRequest): StockResponse {
        val product = productService.getProductById(stockRequest.productId)
        val currentStock = stockRepository.findByProductId(product.productId)

        if (currentStock.isPresent) {
            throw StockAlreadyExistsException("Stock already exists for product: ${product.productId}")
        }

        val newStock = StockEntity(
            productId = product.productId,
            quantity = stockRequest.quantity
        )

        val createdStock = stockRepository.save(newStock)
        return StockResponse(createdStock.productId, createdStock.quantity)
    }

    @Transactional
    fun updateStock(productId: UUID, stockRequest: StockRequest): StockResponse {
        val currentStock = getStock(productId)
        currentStock.quantity = stockRequest.quantity

        val updatedStock = stockRepository.save(currentStock)
        return StockResponse(updatedStock.productId, updatedStock.quantity)
    }

    @Transactional
    fun deleteStock(productId: UUID) {
        val currentStock = getStock(productId)
        stockRepository.delete(currentStock)
    }

    private fun getStock(productId: UUID): StockEntity {
        val product = productService.getProductById(productId)

        return stockRepository.findByProductId(product.productId).orElseThrow {
            StockNotFoundException("No stock found for product: ${product.productId}")
        }
    }
}