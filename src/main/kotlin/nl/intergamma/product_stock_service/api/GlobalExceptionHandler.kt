package nl.intergamma.product_stock_service.api

import nl.intergamma.product_stock_service.exception.InsufficientStockException
import nl.intergamma.product_stock_service.exception.ProductNotFoundException
import nl.intergamma.product_stock_service.exception.ReservationNotFoundException
import nl.intergamma.product_stock_service.exception.StockAlreadyExistsException
import nl.intergamma.product_stock_service.exception.StockNotFoundException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest

@ControllerAdvice
class GlobalExceptionHandler {
    private val logger: Logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(
        value = [ReservationNotFoundException::class,
            InsufficientStockException::class,
            StockAlreadyExistsException::class,
            StockNotFoundException::class,
            ProductNotFoundException::class]
    )
    fun handleBadRequestsExceptions(
        ex: Exception,
        request: WebRequest
    ): ResponseEntity<String> {
        logger.warn("Bad request {}", ex.message)
        return ResponseEntity(ex.message, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(Exception::class)
    fun handleGlobalException(
        ex: Exception,
        request: WebRequest
    ): ResponseEntity<String> {
        logger.error("An unexpected error occurred", ex)
        return ResponseEntity("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR)
    }
}