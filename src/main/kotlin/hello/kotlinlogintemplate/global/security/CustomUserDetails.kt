package hello.kotlinlogintemplate.global.security

import hello.kotlinlogintemplate.member.domain.Member
import hello.kotlinlogintemplate.member.domain.Role
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.*

class CustomUserDetails(
    var memberId: Long,
    var email: String,
    var roleType: Role,
    private var _password: String,
) : UserDetails {

    companion object {
        fun of(member: Member): CustomUserDetails {
            return CustomUserDetails(memberId = member.id, email = member.email, roleType = member.role, _password = member.password)
        }
    }

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return Collections.singleton(SimpleGrantedAuthority("ROLE_${roleType.name}"))
    }

    override fun getPassword(): String {
        return _password
    }

    override fun getUsername(): String {
        return email
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }
}
