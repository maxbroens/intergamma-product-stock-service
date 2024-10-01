package nl.intergamma.product_stock_service.security

enum class UserRole(val role: String) {
    READER("READER"),
    WRITER("WRITER")
}