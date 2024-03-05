package hello.kotlinlogintemplate.global.oauth

import hello.kotlinlogintemplate.global.jwt.TokenProvider
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.core.user.DefaultOAuth2User
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler

class OAuth2LoginSuccessHandler(
    private val tokenProvider: TokenProvider
) : SimpleUrlAuthenticationSuccessHandler() {

    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        try {
            val attributes = (authentication.principal as DefaultOAuth2User).attributes
            val memberId = attributes["id"] as? Long ?: return
            val token = tokenProvider.createToken(memberId, authentication)

            addCookieToResponse(response, "access_token", token.accessToken)
            addCookieToResponse(response, "refresh_token", token.refreshToken)
        } finally {
            clearAuthenticationAttributes(request)
            response.sendRedirect("/loginForm")
        }
    }

    private fun addCookieToResponse(response: HttpServletResponse, cookieName: String, cookieValue: String) {
        val cookie = Cookie(cookieName, cookieValue)
        cookie.path = "/"
        response.addCookie(cookie)
    }
}
