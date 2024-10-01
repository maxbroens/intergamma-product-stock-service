package nl.intergamma.product_stock_service.api.reservation

import nl.intergamma.product_stock_service.service.ReservationService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/reservations")
class ReservationController(
    val reservationService: ReservationService
) {

    @PostMapping
    fun createReservation(@RequestBody reservationRequest: ReservationRequest): ReservationResponse {
        return reservationService.createReservation(reservationRequest)
    }

    @GetMapping("/{reservationId}")
    fun getReservation(@PathVariable reservationId: UUID): ReservationResponse {
        return reservationService.getReservation(reservationId)
    }

    @DeleteMapping("/{reservationId}")
    fun deleteReservation(@PathVariable reservationId: UUID) {
        reservationService.deleteReservation(reservationId)
    }
}