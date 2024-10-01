package nl.intergamma.product_stock_service.security

import org.springframework.boot.autoconfigure.security.servlet.PathRequest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig(
    val securityProperties: SecurityProperties
) {

    companion object {
        const val API_ENDPOINT = "/api/**"
        val SWAGGER_ENDPOINTS = arrayOf("/swagger-ui.html/**", "/v3/api-docs/**", "/swagger-ui/**")
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .authorizeHttpRequests { auth ->
                auth.requestMatchers(PathRequest.toH2Console()).permitAll()
                auth.requestMatchers(*SWAGGER_ENDPOINTS).permitAll()
                auth.requestMatchers(HttpMethod.GET, API_ENDPOINT).hasRole(UserRole.READER.role)
                auth.requestMatchers(HttpMethod.POST, API_ENDPOINT).hasRole(UserRole.WRITER.role)
                auth.requestMatchers(HttpMethod.PUT, API_ENDPOINT).hasRole(UserRole.WRITER.role)
                auth.requestMatchers(HttpMethod.DELETE, API_ENDPOINT).hasRole(UserRole.WRITER.role)
            }
            .headers { headers ->
                headers.frameOptions { it.sameOrigin() }
            }
            .csrf { it.disable() }
            .httpBasic(Customizer.withDefaults())

        return http.build()
    }

    @Bean
    fun userDetailsService(passwordEncoder: PasswordEncoder): UserDetailsService {
        val users: List<UserDetails> = securityProperties.users.map { secureUser ->
            val roles = secureUser.roles.map { role -> UserRole.valueOf(role).role }
            User.builder()
                .username(secureUser.username)
                .password(passwordEncoder.encode(secureUser.password))
                .roles(*roles.toTypedArray())
                .build()
        }

        return InMemoryUserDetailsManager(users)
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
}