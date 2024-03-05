package hello.kotlinlogintemplate.member.application.service

import hello.kotlinlogintemplate.ApiTest
import hello.kotlinlogintemplate.global.exception.error.ErrorCode
import hello.kotlinlogintemplate.member.MemberSteps
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

class MemberServiceTest : ApiTest(){

    @Test
    @DisplayName("회원가입")
    fun signUp() {
        val request = MemberSteps.회원가입요청_생성()

        val response = MemberSteps.회원가입요청(request)

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value())
    }

    @Test
    @DisplayName("회원가입 시 이메일이 중복되면 예외가 발생한다")
    fun signUpWithDuplicatedEmail() {
        MemberSteps.회원가입요청(MemberSteps.회원가입요청_생성())

        val response = MemberSteps.회원가입요청(MemberSteps.회원가입요청_생성())

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value())
        assertThat(response.jsonPath().getString("message")).isEqualTo(ErrorCode.DUPLICATE_EMAIL.message)
    }
}
