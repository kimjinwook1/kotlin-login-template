package hello.kotlinlogintemplate.global.security

import hello.kotlinlogintemplate.member.domain.MemberJdslRepository
import hello.kotlinlogintemplate.member.domain.exception.MemberException
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.math.log

@Service
class CustomUserDetailsService (
    private val memberJdslRepository: MemberJdslRepository,
) : UserDetailsService {

    @Transactional(readOnly = true)
    override fun loadUserByUsername(memberId: String): CustomUserDetails {
        return memberJdslRepository.findAuthInfoById(memberId.toLong())
            ?: throw MemberException.NotExistMemberException()
    }
}
