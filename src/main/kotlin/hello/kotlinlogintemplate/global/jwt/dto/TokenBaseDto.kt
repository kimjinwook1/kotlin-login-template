package hello.kotlinlogintemplate.global.jwt.dto

data class TokenBaseDto(
    var accessToken: String,
    var refreshToken: String
)
