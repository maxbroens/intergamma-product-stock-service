package nl.intergamma.product_stock_service.api.stock

import nl.intergamma.product_stock_service.exception.InsufficientStockException
import nl.intergamma.product_stock_service.service.ReservationService
import nl.intergamma.product_stock_service.service.StockService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/stock/products")
class StockController(
    val stockService: StockService,
    val reservationService: ReservationService
) {

    @PostMapping
    fun addStock(@RequestBody stockRequest: StockRequest) {
        stockService.addStock(stockRequest)
    }

    @GetMapping("/{productId}")
    fun getStock(@PathVariable productId: UUID): StockResponse {
        val stockOfProduct = stockService.getStockOfProduct(productId)
        val activeReservations = reservationService.getActiveReservations(productId)

        val totalReservedQuantity = activeReservations.sumOf { it.quantity }
        val availableStock = stockOfProduct.quantity.minus(totalReservedQuantity)

        val stockIsNegative = availableStock < 0
        if (stockIsNegative) {
            throw InsufficientStockException("Available stock is negative for product: $productId")
        }
        return StockResponse(productId, availableStock)
    }

    @PutMapping("/{productId}")
    fun updateStock(@PathVariable productId: UUID, @RequestBody stockRequest: StockRequest) {
        stockService.updateStock(productId, stockRequest)
    }

    @DeleteMapping("/{productId}")
    fun deleteStock(@PathVariable productId: UUID) {
        stockService.deleteStock(productId)
    }

}