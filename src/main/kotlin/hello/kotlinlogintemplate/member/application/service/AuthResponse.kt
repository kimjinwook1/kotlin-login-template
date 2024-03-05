package hello.kotlinlogintemplate.member.application.service

import hello.kotlinlogintemplate.global.jwt.dto.TokenBaseDto

data class AuthResponse(
    val accessToken: String,
    val refreshToken: String,
) {

    companion object {
        fun of(dto: TokenBaseDto): AuthResponse {
            return AuthResponse(dto.accessToken, dto.refreshToken)
        }
    }
}
