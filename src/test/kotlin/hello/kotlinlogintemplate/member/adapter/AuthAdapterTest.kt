package hello.kotlinlogintemplate.member.adapter

import hello.kotlinlogintemplate.global.jwt.TokenProvider
import hello.kotlinlogintemplate.global.jwt.dto.TokenBaseDto
import hello.kotlinlogintemplate.global.security.CustomUserDetails
import hello.kotlinlogintemplate.member.application.port.AuthPort
import hello.kotlinlogintemplate.member.domain.Member
import hello.kotlinlogintemplate.member.domain.MemberJdslRepository
import hello.kotlinlogintemplate.member.domain.exception.AuthException
import hello.kotlinlogintemplate.member.util.GivenMember
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.crypto.password.PasswordEncoder

class AuthAdapterTest {

    private val memberJdslRepository = mockk<MemberJdslRepository>()
    private val tokenProvider = mockk<TokenProvider>()
    private val managerBuilder = mockk<AuthenticationManagerBuilder>()
    private val passwordEncoder = mockk<PasswordEncoder>()

    private lateinit var authPort: AuthPort
    private lateinit var member: Member

    @BeforeEach
    fun setUp() {
        member = GivenMember.toMember()
        authPort = AuthAdapter(memberJdslRepository, managerBuilder, tokenProvider, passwordEncoder)
    }

    @Test
    @DisplayName("인증에 성공하고 AccessToken을 발급 받는다")
    fun successfulAuthenticationReturnsAccessToken() {
        // given
        val userDetails = CustomUserDetails.of(member).apply {
            every { memberJdslRepository.findAuthInfoByEmail(any()) } returns this
        }
        UsernamePasswordAuthenticationToken(userDetails, "").apply {
            every { managerBuilder.`object`.authenticate(any()) } returns this
        }
        TokenBaseDto("accessToken", "refreshToken").apply {
            every { tokenProvider.createToken(any(), any()) } returns this
        }

        every { passwordEncoder.matches(GivenMember.PASSWORD, userDetails.password) } returns true

        // when
        val accessToken = authPort.auth(GivenMember.EMAIL, GivenMember.PASSWORD).accessToken

        // then
        assertThat(accessToken).isEqualTo("accessToken")
        verify { memberJdslRepository.findAuthInfoByEmail(any()) }
    }

    @Test
    @DisplayName("인증에 실패하면 인증에 실패했습니다 예외 발생")
    fun failedAuthenticationThrowsException() {
        // given
        every { memberJdslRepository.findAuthInfoByEmail(any()) } returns null

        // when & then
        catchException { authPort.auth(GivenMember.EMAIL, GivenMember.PASSWORD).accessToken }.apply {
            assertThat(this).isInstanceOf(AuthException.AuthorizationFailException::class.java)
                .hasMessageContaining("인증에 실패했습니다.: ${GivenMember.EMAIL}")

            verify(exactly = 0) { tokenProvider.createToken(any(), any()) }
            verify(exactly = 0) { managerBuilder.`object`.authenticate(any()) }
        }

    }

    @Test
    @DisplayName("유효한 refreshToken을 갖고 있다면 AccessToken을 재발급 받을 수 있다")
    fun validRefreshTokenAllowsTokenRefresh() {
        // given
        CustomUserDetails.of(member).apply {
            every { tokenProvider.getAuthentication(any()) } returns UsernamePasswordAuthenticationToken(this, "")
        }
        TokenBaseDto("newAccessToken", "newRefreshToken").apply {
            every { tokenProvider.createToken(any(), any()) } returns this
        }
        every { tokenProvider.validateToken(any()) } returns true

        // when & then
        run {
            val tokenBaseDto = authPort.refresh("accessToken", "refreshToken")
            assertThat(tokenBaseDto.accessToken).isEqualTo("newAccessToken")
            verify { tokenProvider.createToken(any(), any()) }
        }
    }

    @Test
    @DisplayName("유효한 refreshToken이 아니라면 리프레시 토큰 인증에 실패했습니다 예외 발생")
    fun invalidRefreshTokenThrowsException() {
        // given
        val originAccessToken = "accessToken"
        val originRefreshToken = "refreshToken"

        every { tokenProvider.validateToken(any()) } returns false

        // when & then
        catchException { authPort.refresh(originAccessToken, originRefreshToken) }.apply {
            assertThat(this)
                .isInstanceOf(AuthException.RefreshInvalidException::class.java)
                .hasMessageContaining("리프레시 토큰 인증에 실패했습니다.: ")

            verify(exactly = 0) { tokenProvider.createToken(any(), any()) }
            verify(exactly = 0) { tokenProvider.getAuthentication(any()) }
        }
    }
}
