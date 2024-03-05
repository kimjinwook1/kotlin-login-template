package hello.kotlinlogintemplate.member.domain

import jakarta.persistence.*

@Entity
class Member(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L,

    @Column(name = "email", unique = true)
    var email: String,

    @Column(name = "password")
    var password: String,

    @Column(name = "name")
    var name: String,

    @Column(name = "provider")
    val provider: String? = null, // kakao, google, naver

    @Column(name = "provider_id")
    val providerId: String? = null, // kakaoId, googleId, naverId

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    var role: Role = Role.USER,
)
