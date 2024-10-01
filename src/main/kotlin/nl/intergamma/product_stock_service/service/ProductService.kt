package nl.intergamma.product_stock_service.service

import nl.intergamma.product_stock_service.api.product.ProductResponse
import nl.intergamma.product_stock_service.exception.ProductNotFoundException
import nl.intergamma.product_stock_service.persistence.repository.ProductRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class ProductService(
    val productRepository: ProductRepository
) {

    @Transactional(readOnly = true)
    fun getProducts(): List<ProductResponse> {
        return productRepository.findAll().map {
            ProductResponse(
                productId = it.id,
                ean = it.ean,
                productName = it.productName,
                description = it.description,
                price = it.price
            )
        }
    }

    @Transactional(readOnly = true)
    fun getProductById(productId: UUID): ProductResponse {
        val product =
            productRepository.findById(productId).orElseThrow { throw ProductNotFoundException("Product not found") }

        return ProductResponse(
            productId = product.id,
            ean = product.ean,
            productName = product.productName,
            description = product.description,
            price = product.price
        )
    }
}