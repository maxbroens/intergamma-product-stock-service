package nl.intergamma.product_stock_service.service

import nl.intergamma.product_stock_service.api.reservation.ReservationRequest
import nl.intergamma.product_stock_service.api.stock.StockResponse
import nl.intergamma.product_stock_service.exception.InsufficientStockException
import nl.intergamma.product_stock_service.exception.ReservationNotFoundException
import nl.intergamma.product_stock_service.persistence.entity.ReservationEntity
import nl.intergamma.product_stock_service.persistence.repository.ReservationRepository
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
import java.util.Optional
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class ReservationServiceTest {

    @Mock
    lateinit var reservationRepository: ReservationRepository

    @Mock
    lateinit var stockService: StockService

    @InjectMocks
    lateinit var reservationService: ReservationService

    @Test
    fun `createReservation should create and return reservation response`() {
        val productId = UUID.randomUUID()
        val reservationRequest = ReservationRequest(productId, 5)
        val availableStock = StockResponse(productId, 10)
        val reservationEntity = ReservationEntity(productId = productId, quantity = 5)
        val savedEntity =
            reservationEntity.copy(id = UUID.randomUUID(), reservedAt = reservationEntity.reservedAt.minusMinutes(1))

        whenever(stockService.getStockOfProduct(productId)).thenReturn(availableStock)
        whenever(reservationRepository.findAllByProductIdAndReservedAtGreaterThanEqual(any(), any())).thenReturn(
            listOf(reservationEntity)
        )
        whenever(reservationRepository.save(any<ReservationEntity>())).thenReturn(savedEntity)

        val response = reservationService.createReservation(reservationRequest)

        assertEquals(productId, response.productId)
        assertEquals(5, response.quantity)
    }

    @Test
    fun `createReservation should throw InsufficientStockException if stock is insufficient`() {
        val productId = UUID.randomUUID()
        val reservationRequest = ReservationRequest(productId, 15)
        val availableStock = StockResponse(productId, 10)

        whenever(stockService.getStockOfProduct(productId)).thenReturn(availableStock)
        whenever(
            reservationRepository.findAllByProductIdAndReservedAtGreaterThanEqual(any(), any())
        ).thenReturn(emptyList())

        assertThrows(InsufficientStockException::class.java) {
            reservationService.createReservation(reservationRequest)
        }
    }

    @Test
    fun `getActiveReservations should return list of active reservations`() {
        val productId = UUID.randomUUID()
        val reservationEntity = ReservationEntity(productId = productId, quantity = 5)
        val activeReservations = listOf(reservationEntity)

        whenever(reservationRepository.findAllByProductIdAndReservedAtGreaterThanEqual(any(), any())).thenReturn(
            activeReservations
        )

        val reservations = reservationService.getActiveReservations(productId)

        assertEquals(1, reservations.size)
        assertEquals(productId, reservations[0].productId)
    }

    @Test
    fun `getReservation should return reservation response`() {
        val productId = UUID.randomUUID()
        val reservationEntity = ReservationEntity(productId = productId, quantity = 5)

        val reservationId = reservationEntity.id

        whenever(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservationEntity))

        val response = reservationService.getReservation(reservationId)

        assertEquals(reservationId, response.id)
        assertEquals(5, response.quantity)
    }

    @Test
    fun `getReservation should throw ReservationNotFoundException if reservation not found`() {
        val reservationId = UUID.randomUUID()

        whenever(reservationRepository.findById(reservationId)).thenReturn(Optional.empty())

        assertThrows(ReservationNotFoundException::class.java) {
            reservationService.getReservation(reservationId)
        }
    }

    @Test
    fun `deleteReservation should delete reservation`() {
        val productId = UUID.randomUUID()
        val reservationEntity = ReservationEntity(productId = productId, quantity = 5)

        val reservationId = reservationEntity.id

        whenever(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservationEntity))

        reservationService.deleteReservation(reservationId)

        verify(reservationRepository).delete(reservationEntity)
    }
}