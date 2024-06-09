package com.example.musala.security

import com.example.musala.services.auth.services.JwtService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfig {
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun authenticationManager(config: AuthenticationConfiguration): AuthenticationManager {
        return config.authenticationManager
    }

    @Bean
    fun securityFilterChain(
        http: HttpSecurity,
        jwtService: JwtService,
        userDetailsService: UserDetailsService,
    ): SecurityFilterChain {
        val authProvider = DaoAuthenticationProvider().apply {
            setPasswordEncoder(passwordEncoder())
            setUserDetailsService(userDetailsService)
        }
        val jwtAuthenticationFilter = JwtAuthenticationFilter(
            jwtService = jwtService,
            userDetailsService = userDetailsService,
        )
        http
            .authenticationProvider(authProvider)
        http {
            csrf {
                disable()
            }
            authorizeRequests {
                authorize(HttpMethod.POST, "/auth", permitAll)
                authorize(HttpMethod.POST, "/users", permitAll)
                authorize(HttpMethod.GET, "/events", permitAll)
                authorize(anyRequest, authenticated)
            }
            sessionManagement {
                sessionCreationPolicy = SessionCreationPolicy.STATELESS
            }
            addFilterBefore<UsernamePasswordAuthenticationFilter>(jwtAuthenticationFilter)
            exceptionHandling {
                authenticationEntryPoint = AuthenticationEntryPoint { _, response, _ ->
                    response.setHeader("WWW-Authenticate", "Bearer token68")
                    response.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.reasonPhrase)
                }
            }
        }

        return http.build()
    }
}