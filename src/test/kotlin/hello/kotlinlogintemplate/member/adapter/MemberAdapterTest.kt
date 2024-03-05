package hello.kotlinlogintemplate.member.adapter

import hello.kotlinlogintemplate.member.domain.Member
import hello.kotlinlogintemplate.member.domain.MemberRepository
import hello.kotlinlogintemplate.member.util.GivenMember
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class MemberAdapterTest {

    private val memberRepository = mockk<MemberRepository>()

    private lateinit var memberAdapter: MemberAdapter
    private lateinit var member: Member

    @BeforeEach
    fun setUp() {
        memberAdapter = MemberAdapter(memberRepository)
        member = GivenMember.toMember()
    }

    @Test
    @DisplayName("회원 정보 저장에 성공해야한다")
    fun successfulSaveMember() {
        // given
        every { memberRepository.save(any()) } returns member

        // when
        memberAdapter.save(member)

        // then
        verify { memberRepository.save(any()) }
    }

    @Test
    @DisplayName("이메일로 회원 정보를 조회할 수 있어야한다")
    fun successfulFindByEmail() {
        // given
        every { memberRepository.findByEmail(any()) } returns member

        // when
        memberAdapter.findByEmail(member.email)

        // then
        verify { memberRepository.findByEmail(any()) }
    }
}
