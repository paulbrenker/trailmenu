package com.nutrike.core.config

import com.nutrike.core.config.AuthenticationConfig.Companion.ADMIN_RIGHTS_REQUIRED
import com.nutrike.core.config.AuthenticationConfig.Companion.AUTHENTICATION_EXCLUDE
import com.nutrike.core.entity.RoleType
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtFilter: JwtFilter,
    private val jwtAuthenticationEntryPoint: JwtAuthenticationEntryPoint,
) {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .exceptionHandling { it.authenticationEntryPoint(jwtAuthenticationEntryPoint) }
            .authorizeHttpRequests {
                AUTHENTICATION_EXCLUDE.map { (path, method) -> it.requestMatchers(method, path).permitAll() }
                ADMIN_RIGHTS_REQUIRED.map { (path, method) ->
                    it.requestMatchers(method, path).hasRole(
                        RoleType.ADMIN
                            .toString(),
                    )
                }
                it.anyRequest().authenticated()
            }.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter::class.java)
        return http.build()
    }
}
