package hello.kotlinlogintemplate.member.application.service

data class RefreshRequest(
    val accessToken: String,
    val refreshToken: String
)
