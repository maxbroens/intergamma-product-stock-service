package nl.intergamma.product_stock_service.service

import nl.intergamma.product_stock_service.api.reservation.ReservationRequest
import nl.intergamma.product_stock_service.api.reservation.ReservationResponse
import nl.intergamma.product_stock_service.exception.InsufficientStockException
import nl.intergamma.product_stock_service.exception.ReservationNotFoundException
import nl.intergamma.product_stock_service.persistence.entity.ReservationEntity
import nl.intergamma.product_stock_service.persistence.repository.ReservationRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Service
class ReservationService(
    val reservationRepository: ReservationRepository,
    val stockService: StockService
) {

    companion object {
        const val RESERVATION_LIFE_SPAN_IN_MIN = 5L
    }

    @Transactional
    fun createReservation(reservationRequest: ReservationRequest): ReservationResponse {
        val productId = reservationRequest.productId
        val availableStock = stockService.getStockOfProduct(productId)
        val activeReservations = getActiveReservationsForProduct(productId)
        val totalReservedQuantity = activeReservations.sumOf { it.quantity }

        val isStockSufficientForReservation =
            availableStock.quantity.minus(totalReservedQuantity) >= reservationRequest.quantity

        if (!isStockSufficientForReservation) {
            throw InsufficientStockException("Not enough stock available for product: $productId")
        }

        val createdReservation = reservationRepository.save(
            ReservationEntity(
                productId = reservationRequest.productId,
                quantity = reservationRequest.quantity
            )
        )

        return ReservationResponse(
            id = createdReservation.id,
            productId = createdReservation.productId,
            quantity = createdReservation.quantity,
            reservedAt = createdReservation.reservedAt
        )
    }

    @Transactional(readOnly = true)
    fun getActiveReservations(productId: UUID): List<ReservationResponse> {
        return getActiveReservationsForProduct(productId).map { reservation ->
            ReservationResponse(
                id = reservation.id,
                productId = reservation.productId,
                quantity = reservation.quantity,
                reservedAt = reservation.reservedAt
            )
        }
    }

    @Transactional(readOnly = true)
    fun getReservation(reservationId: UUID): ReservationResponse {
        val reservation = reservationRepository
            .findById(reservationId)
            .orElseThrow {
                ReservationNotFoundException("No reservation found for id: $reservationId")
            }

        return ReservationResponse(
            id = reservation.id,
            productId = reservation.productId,
            quantity = reservation.quantity,
            reservedAt = reservation.reservedAt
        )
    }

    @Transactional
    fun deleteReservation(reservationId: UUID) {
        val reservation = reservationRepository
            .findById(reservationId)

        reservation.ifPresent { reservationRepository.delete(it) }
    }

    private fun getActiveReservationsForProduct(productId: UUID): List<ReservationEntity> {
        val reservationLifeSpan = LocalDateTime.now().minusMinutes(RESERVATION_LIFE_SPAN_IN_MIN)

        return reservationRepository.findAllByProductIdAndReservedAtGreaterThanEqual(
            productId = productId,
            reservedAt = reservationLifeSpan
        )
    }
}