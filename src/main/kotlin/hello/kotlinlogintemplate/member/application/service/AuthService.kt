package hello.kotlinlogintemplate.member.application.service

import hello.kotlinlogintemplate.member.application.port.AuthPort
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auths")
@Transactional
class AuthService(
    private val authPort: AuthPort
) {
    @PostMapping
    fun auth(@Valid @RequestBody request: AuthRequest): ResponseEntity<AuthResponse> {
        return ResponseEntity.ok(AuthResponse.of(authPort.auth(request.email, request.password)))
    }

    @PostMapping("/refresh")
    fun refresh(@Valid @RequestBody request: RefreshRequest): ResponseEntity<AuthResponse> {
        return ResponseEntity.ok(AuthResponse.of(authPort.refresh(request.accessToken, request.refreshToken)))
    }

}
