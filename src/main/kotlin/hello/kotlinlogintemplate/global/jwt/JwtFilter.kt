package hello.kotlinlogintemplate.global.jwt

import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter

class JwtFilter(private val tokenProvider: TokenProvider) : OncePerRequestFilter() {
    // OncePerRequestFilter 상속받아 매 요청마다 필터 실행

    private val jwtLogger = LoggerFactory.getLogger(JwtFilter::class.java)
    private val AUTHORIZATION_HEADER = "Authorization"

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        val jwt = resolveToken(request)

        jwt?.let {
            if (tokenProvider.validateToken(it)) {
                setAuthentication(it, request.requestURI)
            } else {
                jwtLogger.debug("유효한 JWT 토큰이 없습니다, uri: ${request.requestURI}")
            }
        }

        chain.doFilter(request, response)
    }

    private fun resolveToken(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader(AUTHORIZATION_HEADER)
        return bearerToken?.removePrefix("Bearer ")
    }

    private fun setAuthentication(token: String, requestURI: String) {
        // tokenProvier에서 토큰을 이용해 Authentication 객체를 가져온다.
        val authentication = tokenProvider.getAuthentication(token)
        // SecurityContext에 Authentication 객체를 저장한다.
        SecurityContextHolder.getContext().authentication = authentication
        jwtLogger.debug("Security Context에 '${authentication.name}' 인증 정보를 저장했습니다, uri: $requestURI")
    }

}
