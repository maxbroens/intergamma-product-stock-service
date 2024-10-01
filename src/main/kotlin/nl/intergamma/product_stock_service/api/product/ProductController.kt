package nl.intergamma.product_stock_service.api.product

import nl.intergamma.product_stock_service.service.ProductService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/products")
class ProductController(
    val productService: ProductService
) {

    @GetMapping
    fun getProducts(): List<ProductResponse> {
        return productService.getProducts()
    }

}