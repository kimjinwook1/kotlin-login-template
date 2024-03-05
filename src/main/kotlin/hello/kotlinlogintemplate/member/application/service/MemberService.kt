package hello.kotlinlogintemplate.member.application.service

import hello.kotlinlogintemplate.member.application.port.MemberPort
import hello.kotlinlogintemplate.member.domain.exception.MemberException
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/members")
@Transactional
class MemberService (
    private val memberPort: MemberPort,
    private val passwordEncoder: PasswordEncoder
) {

    @PostMapping
    fun save(@Valid @RequestBody request: MemberSaveRequest): ResponseEntity<Unit> {
        memberPort.findByEmail(request.email)?.let {
            throw MemberException.DuplicateEmailException()
        }

        request.toMember(passwordEncoder.encode(request.password))
            .also { memberPort.save(it) }

        return ResponseEntity.status(HttpStatus.CREATED).build()
    }
}
