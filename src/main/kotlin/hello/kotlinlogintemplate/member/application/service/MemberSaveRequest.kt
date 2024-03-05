package hello.kotlinlogintemplate.member.application.service

import hello.kotlinlogintemplate.member.domain.Member
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class MemberSaveRequest(
    @field:Email(message = "이메일 형식이 아닙니다.")
    @field:NotBlank(message = "이메일을 입력해주세요.")
    val email: String,
    @field:NotBlank(message = "비밀번호를 입력해주세요.")
    val password: String,
    @field:NotBlank(message = "이름을 입력해주세요.")
    val name: String,
) {
    fun toMember(encodedPassword: String): Member {
        return Member(
            email = this.email,
            password = encodedPassword,
            name = this.name,
        )
    }
}
