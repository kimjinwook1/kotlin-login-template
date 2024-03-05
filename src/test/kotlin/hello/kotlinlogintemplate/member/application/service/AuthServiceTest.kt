package hello.kotlinlogintemplate.member.application.service

import hello.kotlinlogintemplate.ApiTest
import hello.kotlinlogintemplate.member.AuthSteps
import hello.kotlinlogintemplate.member.MemberSteps
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

class AuthServiceTest : ApiTest() {

    @Test
    @DisplayName("토큰 발급")
    fun issueToken() {
        MemberSteps.회원가입요청(MemberSteps.회원가입요청_생성())

        val request = AuthSteps.토큰발급요청_생성()
        val response = AuthSteps.토큰발급요청(request)

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value())
    }

    @Test
    @DisplayName("토큰 갱신")
    fun refreshToken() {
        MemberSteps.회원가입요청(MemberSteps.회원가입요청_생성())
        val authResponse = AuthSteps.토큰발급요청(AuthSteps.토큰발급요청_생성()).`as`(AuthResponse::class.java)

        val request = AuthSteps.토큰갱신요청_생성(authResponse.accessToken, authResponse.refreshToken)
        val response = AuthSteps.토큰갱신요청(request)

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value())
    }
}
