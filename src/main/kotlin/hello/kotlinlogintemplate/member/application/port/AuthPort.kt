package hello.kotlinlogintemplate.member.application.port

import hello.kotlinlogintemplate.global.jwt.dto.TokenBaseDto

interface AuthPort {
    fun auth(email: String, password: String): TokenBaseDto
    fun refresh(accessToken: String, refreshToken: String): TokenBaseDto
}
