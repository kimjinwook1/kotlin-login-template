package hello.kotlinlogintemplate.global.jwt

import hello.kotlinlogintemplate.global.jwt.dto.TokenBaseDto
import hello.kotlinlogintemplate.global.security.CustomUserDetailsService
import io.jsonwebtoken.*
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import java.security.Key
import java.util.*
import java.util.stream.Collectors

@Component
class TokenProvider(
    @param:Value("\${jwt.secret}") private val secret: String,
    @Value("\${jwt.accessToken-validity-in-seconds}") private val accessTokenValidityInSeconds: Long,
    @Value("\${jwt.refreshToken-validity-in-seconds}") private val refreshTokenValidityInSeconds: Long,
    private val customUserDetailsService: CustomUserDetailsService
) : InitializingBean {

    private val log = LoggerFactory.getLogger(TokenProvider::class.java)
    private val accessTokenValidityInMilliSeconds: Long = accessTokenValidityInSeconds * 1000
    private val refreshTokenValidityInMilliSeconds: Long = refreshTokenValidityInSeconds * 1000
    private var key: Key? = null

    companion object {
        private const val AUTHORITIES_KEY = "Authorization"
    }

    override fun afterPropertiesSet() {
        key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret))
    }

    fun createToken(memberId: Long, authentication: Authentication): TokenBaseDto {
        val authorities = getAuthorities(authentication)

        val accessTokenExpiredTime = Date(Date().time + accessTokenValidityInMilliSeconds)
        val refreshTokenExpiredTime = Date(Date().time + refreshTokenValidityInMilliSeconds)

        val accessToken = createAccessToken(memberId, authorities, accessTokenExpiredTime)
        val refreshToken = createRefreshToken(refreshTokenExpiredTime)

        return TokenBaseDto(accessToken = accessToken, refreshToken = refreshToken)
    }

    private fun createAccessToken(memberId: Long, authorities: String, expirationTime: Date): String {
        return Jwts.builder()
            .claim("memberId", memberId.toString())
            .claim(AUTHORITIES_KEY, authorities)
            .setExpiration(expirationTime)
            .signWith(key, SignatureAlgorithm.HS512)
            .compact()
    }

    private fun createRefreshToken(expirationTime: Date): String {
        return Jwts.builder()
            .setExpiration(expirationTime)
            .signWith(key, SignatureAlgorithm.HS512)
            .compact()
    }

    private fun getAuthorities(authentication: Authentication): String {
        return authentication.authorities.stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(","))
    }

    fun getAuthentication(token: String): Authentication {
        // 토큰을 이용해 Claims 객체를 생성한다.
        val claims = try {
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .body
        } catch (e: ExpiredJwtException) {
            e.claims
        }

        // 권한 정보를 이용해 Authentication 객체를 가져온다.
        val authorities = claims[AUTHORITIES_KEY]
            .toString()
            .split(",")
            .filter { it.isNotEmpty() }
            .map { SimpleGrantedAuthority(it) }

        val memberId = claims["memberId"]

        val userDetails = customUserDetailsService.loadUserByUsername(memberId = memberId.toString())

        return UsernamePasswordAuthenticationToken(userDetails, "", authorities)
    }

    fun validateToken(token: String): Boolean {
        return runCatching {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token)
            true
        }.getOrElse {
            handleTokenException(it)
            false
        }
    }

    private fun handleTokenException(e: Throwable) {
        when (e) {
            is io.jsonwebtoken.security.SecurityException, is MalformedJwtException -> log.error("잘못된 JWT 서명입니다.")
            is ExpiredJwtException -> log.error("만료된 JWT 토큰입니다.")
            is UnsupportedJwtException -> log.error("지원되지 않는 JWT 토큰입니다.")
            is IllegalArgumentException -> log.error("JWT 토큰이 잘못되었습니다.")
            else -> log.error("알 수 없는 JWT 토큰입니다.", e)
        }
    }
}
