package hello.kotlinlogintemplate.member.util

import hello.kotlinlogintemplate.member.domain.Member

object GivenMember {
    const val EMAIL = "jinwook@test.com"
    const val PASSWORD = "1234"
    const val NAME = "김진욱"

    fun toMember() =
        Member(
            email = this.EMAIL,
            password = this.PASSWORD,
            name = this.NAME,
        )
}
