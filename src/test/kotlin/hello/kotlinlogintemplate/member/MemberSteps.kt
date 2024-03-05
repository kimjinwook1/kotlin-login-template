package hello.kotlinlogintemplate.member

import hello.kotlinlogintemplate.member.application.service.MemberSaveRequest
import io.restassured.RestAssured
import io.restassured.response.ExtractableResponse
import io.restassured.response.Response
import org.springframework.http.MediaType

class MemberSteps {

    companion object {
        fun 회원가입요청_생성(): MemberSaveRequest {
            return MemberSaveRequest(
                email = "jinwook@test.com",
                password = "1234",
                name = "jinwook",
            )
        }

        fun 회원가입요청(request: MemberSaveRequest): ExtractableResponse<Response> {
            return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .`when`()
                .post("/members")
                .then()
                .log().all().extract()
        }

    }
}
