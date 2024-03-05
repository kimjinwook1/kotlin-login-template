package hello.kotlinlogintemplate.member.domain

import org.springframework.data.jpa.repository.JpaRepository

interface MemberRepository : JpaRepository<Member, Long> {
    fun findByEmail(email: String): Member?
    fun findByEmailAndProvider(email: String, provider: String): Member?
}
