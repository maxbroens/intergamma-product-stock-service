package nl.intergamma.product_stock_service.api.reservation

import nl.intergamma.product_stock_service.service.ReservationService
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.time.LocalDateTime
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class ReservationControllerTest {

    @Mock
    lateinit var reservationService: ReservationService

    @InjectMocks
    lateinit var reservationController: ReservationController

    private val mockMvc: MockMvc by lazy {
        MockMvcBuilders.standaloneSetup(reservationController).build()
    }

    companion object {
        const val RESERVATION_API = "/api/reservations"
    }

    @Test
    fun `createReservation should call reservationService createReservation`() {
        val reservationRequest = ReservationRequest(UUID.randomUUID(), 5)
        val reservationResponse = ReservationResponse(
            UUID.randomUUID(),
            reservationRequest.productId,
            reservationRequest.quantity,
            LocalDateTime.now()
        )

        whenever(reservationService.createReservation(any())).thenReturn(reservationResponse)

        mockMvc.perform(
            post(RESERVATION_API)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"productId": "${reservationRequest.productId}", "quantity": ${reservationRequest.quantity}}""")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(reservationResponse.id.toString()))
            .andExpect(jsonPath("$.productId").value(reservationResponse.productId.toString()))
            .andExpect(jsonPath("$.quantity").value(reservationResponse.quantity))

        verify(reservationService).createReservation(any())
    }

    @Test
    fun `getReservation should return reservation response`() {
        val reservationId = UUID.randomUUID()
        val reservationResponse = ReservationResponse(reservationId, UUID.randomUUID(), 5, LocalDateTime.now())

        whenever(reservationService.getReservation(reservationId)).thenReturn(reservationResponse)

        mockMvc.perform(get("$RESERVATION_API/$reservationId"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(reservationResponse.id.toString()))
            .andExpect(jsonPath("$.productId").value(reservationResponse.productId.toString()))
            .andExpect(jsonPath("$.quantity").value(reservationResponse.quantity))

        verify(reservationService).getReservation(reservationId)
    }

    @Test
    fun `deleteReservation should call reservationService deleteReservation`() {
        val reservationId = UUID.randomUUID()

        mockMvc.perform(delete("$RESERVATION_API/$reservationId"))
            .andExpect(status().isOk)

        verify(reservationService).deleteReservation(reservationId)
    }
}