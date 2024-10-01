package nl.intergamma.product_stock_service.api.stock

import jakarta.servlet.ServletException
import nl.intergamma.product_stock_service.api.reservation.ReservationResponse
import nl.intergamma.product_stock_service.exception.InsufficientStockException
import nl.intergamma.product_stock_service.service.ReservationService
import nl.intergamma.product_stock_service.service.StockService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.time.LocalDateTime
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class StockControllerTest {

    @Mock
    lateinit var stockService: StockService

    @Mock
    lateinit var reservationService: ReservationService

    @InjectMocks
    lateinit var stockController: StockController

    private val mockMvc: MockMvc by lazy {
        MockMvcBuilders.standaloneSetup(stockController).build()
    }

    companion object {
        const val STOCK_API = "/api/stock/products"
    }

    @Test
    fun `addStock should call stockService addStock`() {
        val stockRequest = StockRequest(UUID.randomUUID(), 10)

        mockMvc.perform(
            post(STOCK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"productId": "${stockRequest.productId}", "quantity": ${stockRequest.quantity}}""")
        )
            .andExpect(status().isOk)

        verify(stockService).addStock(any())
    }

    @Test
    fun `getStock should return stock response`() {
        val productId = UUID.randomUUID()
        val reservationId = UUID.randomUUID()
        val stockResponse = StockResponse(productId, 10)
        val reservationResponse =
            ReservationResponse(reservationId, productId, 5, LocalDateTime.now())

        whenever(stockService.getStockOfProduct(productId)).thenReturn(stockResponse)
        whenever(reservationService.getActiveReservations(productId)).thenReturn(listOf(reservationResponse))

        mockMvc.perform(get("$STOCK_API/$productId"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.productId").value(productId.toString()))
            .andExpect(jsonPath("$.quantity").value(5))

        verify(stockService).getStockOfProduct(productId)
        verify(reservationService).getActiveReservations(productId)
    }

    @Test
    fun `getStock should throw InsufficientStockException if available stock is negative`() {
        val productId = UUID.randomUUID()
        val reservationId = UUID.randomUUID()
        val stockResponse = StockResponse(productId, 5)
        val reservationResponse =
            ReservationResponse(reservationId, productId, 6, LocalDateTime.now())

        whenever(stockService.getStockOfProduct(productId)).thenReturn(stockResponse)
        whenever(reservationService.getActiveReservations(productId)).thenReturn(listOf(reservationResponse))

        val exception = assertThrows(ServletException::class.java) {
            mockMvc.perform(get("$STOCK_API/$productId"))
                .andExpect(status().isBadRequest)
        }

        assertEquals(InsufficientStockException::class.java, exception.cause!!::class.java)

        verify(stockService).getStockOfProduct(productId)
        verify(reservationService).getActiveReservations(productId)
    }

    @Test
    fun `updateStock should call stockService updateStock`() {
        val productId = UUID.randomUUID()
        val stockRequest = StockRequest(productId, 20)

        mockMvc.perform(
            put("$STOCK_API/$productId")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"productId": "$productId", "quantity": ${stockRequest.quantity}}""")
        )
            .andExpect(status().isOk)

        verify(stockService).updateStock(any(), any())
    }

    @Test
    fun `deleteStock should call stockService deleteStock`() {
        val productId = UUID.randomUUID()

        mockMvc.perform(delete(STOCK_API + "/$productId"))
            .andExpect(status().isOk)

        verify(stockService).deleteStock(productId)
    }
}