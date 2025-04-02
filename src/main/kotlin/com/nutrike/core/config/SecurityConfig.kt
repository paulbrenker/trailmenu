package com.nutrike.core.config

import com.nutrike.core.config.AuthenticationConfig.Companion.ADMIN_RIGHTS_REQUIRED
import com.nutrike.core.config.AuthenticationConfig.Companion.AUTHENTICATION_EXCLUDE
import com.nutrike.core.config.handler.CustomAccessDeniedHandler
import com.nutrike.core.entity.RoleType
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfigurationSource

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtFilter: JwtFilter,
    private val accessDeniedHandler: CustomAccessDeniedHandler,
    private val corsConfigurationSource: CorsConfigurationSource,
) {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .cors { it.configurationSource(corsConfigurationSource) }
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests {
                AUTHENTICATION_EXCLUDE.map { (path, method) ->
                    it.requestMatchers(method, path).permitAll()
                }
                ADMIN_RIGHTS_REQUIRED.map { (path, method) ->
                    it.requestMatchers(method, path).hasRole(
                        RoleType.ADMIN
                            .toString(),
                    )
                }
                it.anyRequest().authenticated()
            }.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter::class.java)
            .exceptionHandling { it.accessDeniedHandler(accessDeniedHandler) }
        return http.build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()
}
