package nl.intergamma.product_stock_service.security

data class SecureUser(
    val username: String,
    val password: String,
    val roles: List<String>
)
