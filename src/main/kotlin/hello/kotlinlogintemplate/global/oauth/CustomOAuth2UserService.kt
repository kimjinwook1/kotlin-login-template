package hello.kotlinlogintemplate.global.oauth

import hello.kotlinlogintemplate.member.domain.Member
import hello.kotlinlogintemplate.member.domain.MemberRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService
import org.springframework.security.oauth2.core.user.DefaultOAuth2User
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service

@Service
class CustomOAuth2UserService(
    private val memberRepository: MemberRepository,
) : OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private val oAuth2UserService: OAuth2UserService<OAuth2UserRequest, OAuth2User> = DefaultOAuth2UserService()

    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        val attributes = getAttributes(userRequest)
        val member = getMember(attributes)
        val authorities = getAuthorities(member)

        val attributesWithId = attributes.toMutableMap().apply {
            put("id", member.id)
        }

        return DefaultOAuth2User(authorities, attributesWithId, "id")
    }

    private fun getAttributes(userRequest: OAuth2UserRequest): Map<String, Any> {
        val oAuth2User = oAuth2UserService.loadUser(userRequest)
        return createOAuth2Attributes(userRequest, oAuth2User)
    }

    private fun getMember(attributes: Map<String, Any>): Member {
        val email = attributes["email"] as String
        val provider = attributes["provider"] as String
        val providerId = attributes["providerId"] as String

        return getMemberByEmail(email, provider, providerId)
    }

    private fun getAuthorities(member: Member): Set<SimpleGrantedAuthority> {
        return setOf(SimpleGrantedAuthority("ROLE_${member.role.name}"))
    }

    private fun getMemberByEmail(email: String, provider: String, providerId: String): Member {
        return memberRepository.findByEmailAndProvider(email, provider)
            ?: createAndSaveMember(email, provider, providerId)
    }

    private fun createAndSaveMember(email: String, provider: String, providerId: String): Member {
        return memberRepository.save(
            Member(
                email = email,
                password = "defaultPassword",
                name = "defaultName",
                provider = provider,
                providerId = providerId
            )
        )
    }

    private fun createOAuth2Attributes(userRequest: OAuth2UserRequest, oAuth2User: OAuth2User): Map<String, Any> {
        val registrationId = getRegistrationId(userRequest)
        val userNameAttributeName = getUserNameAttributeName(userRequest)
        return OAuth2Attribute.of(registrationId, userNameAttributeName, oAuth2User.attributes).convertToMap()
    }

    private fun getRegistrationId(userRequest: OAuth2UserRequest): String {
        return userRequest.clientRegistration.registrationId
    }

    private fun getUserNameAttributeName(userRequest: OAuth2UserRequest): String {
        return userRequest.clientRegistration.providerDetails.userInfoEndpoint.userNameAttributeName
    }
}
