package hello.kotlinlogintemplate.member.adapter

import hello.kotlinlogintemplate.member.application.port.MemberPort
import hello.kotlinlogintemplate.member.domain.Member
import hello.kotlinlogintemplate.member.domain.MemberRepository
import org.springframework.stereotype.Component

@Component
class MemberAdapter(
    private val memberRepository: MemberRepository
) : MemberPort {

    override fun findByEmail(email: String): Member? {
        return memberRepository.findByEmail(email)
    }

    override fun save(member: Member) {
        memberRepository.save(member)
    }
}
