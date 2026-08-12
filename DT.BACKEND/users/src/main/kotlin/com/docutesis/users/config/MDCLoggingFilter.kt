package com.docutesis.users.config

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class MDCLoggingFilter : OncePerRequestFilter() {

    private val log = LoggerFactory.getLogger(MDCLoggingFilter::class.java)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        log.info("event=http.request | msg={} {}", request.method, request.requestURI)

        try {
            filterChain.doFilter(request, response)
        } finally {
            val auth = SecurityContextHolder.getContext().authentication
            val sub = if (auth != null && auth.principal is Jwt) {
                (auth.principal as Jwt).subject
            } else {
                "anonimo"
            }
            MDC.put("sub", sub)
            log.info("event=http.response | msg={} {} {}", response.status, request.method, request.requestURI)
            MDC.remove("sub")
        }
    }
}