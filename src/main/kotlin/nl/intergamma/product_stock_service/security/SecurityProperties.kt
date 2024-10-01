package nl.intergamma.product_stock_service.security

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("security")
data class SecurityProperties(
    val users: List<SecureUser>
)
