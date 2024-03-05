package hello.kotlinlogintemplate.member

import hello.kotlinlogintemplate.member.application.service.AuthRequest
import hello.kotlinlogintemplate.member.application.service.RefreshRequest
import io.restassured.RestAssured
import io.restassured.response.ExtractableResponse
import io.restassured.response.Response
import org.springframework.http.MediaType

class AuthSteps {

    companion object {
        fun 토큰발급요청_생성(): AuthRequest {
            return AuthRequest(
                email = "jinwook@test.com",
                password = "1234",
            )
        }

        fun 토큰발급요청(request: AuthRequest): ExtractableResponse<Response> {
            return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .`when`()
                .post("/auths")
                .then()
                .log().all().extract()
        }

        fun 토큰갱신요청_생성(accessToken: String, refreshToken: String): RefreshRequest {
            return RefreshRequest(
                accessToken = accessToken,
                refreshToken = refreshToken,
            )
        }

        fun 토큰갱신요청(refreshRequest: RefreshRequest): ExtractableResponse<Response> {
            return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(refreshRequest)
                .`when`()
                .post("/auths/refresh")
                .then()
                .log().all().extract()
        }
    }
}
