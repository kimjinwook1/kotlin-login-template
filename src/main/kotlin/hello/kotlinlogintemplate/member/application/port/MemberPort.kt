package hello.kotlinlogintemplate.member.application.port

import hello.kotlinlogintemplate.member.domain.Member

interface MemberPort {
    fun save(member: Member)
    fun findByEmail(email: String): Member?
}
