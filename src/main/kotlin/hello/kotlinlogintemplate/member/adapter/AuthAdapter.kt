package hello.kotlinlogintemplate.member.adapter

import hello.kotlinlogintemplate.global.jwt.TokenProvider
import hello.kotlinlogintemplate.global.jwt.dto.TokenBaseDto
import hello.kotlinlogintemplate.global.security.CustomUserDetails
import hello.kotlinlogintemplate.member.application.port.AuthPort
import hello.kotlinlogintemplate.member.domain.MemberJdslRepository
import hello.kotlinlogintemplate.member.domain.exception.AuthException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class AuthAdapter(
    private val memberJdslRepository: MemberJdslRepository,
    private val managerBuilder: AuthenticationManagerBuilder,
    private val tokenProvider: TokenProvider,
    private val passwordEncoder: PasswordEncoder,
) : AuthPort {
    override fun auth(email: String, password: String): TokenBaseDto {
        val userDetails = getUserDetailsBy(email)
        val authentication = authenticateUser(userDetails, password)

        return tokenProvider.createToken(userDetails.memberId, authentication)
    }

    private fun getUserDetailsBy(email: String) =
        memberJdslRepository.findAuthInfoByEmail(email)
            ?: throw AuthException.AuthorizationFailException(email)

    private fun authenticateUser(userDetails: UserDetails, password: String): Authentication {
        if (!passwordEncoder.matches(password, userDetails.password)) {
            throw AuthException.AuthorizationFailException(userDetails.username)
        }
        val authToken = UsernamePasswordAuthenticationToken(userDetails, password)
        return managerBuilder.`object`.authenticate(authToken)
    }

    override fun refresh(accessToken: String, refreshToken: String): TokenBaseDto {
        validateBy(refreshToken)
        val authentication = getAuthenticationFrom(accessToken)
        val userDetails = authentication.principal as CustomUserDetails

        return tokenProvider.createToken(userDetails.memberId, authentication)
    }

    private fun validateBy(refreshToken: String) {
        if (!tokenProvider.validateToken(refreshToken)) {
            throw AuthException.RefreshInvalidException()
        }
    }
    private fun getAuthenticationFrom(token: String): Authentication = tokenProvider.getAuthentication(token)

}
