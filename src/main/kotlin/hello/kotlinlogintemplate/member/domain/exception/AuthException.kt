package hello.kotlinlogintemplate.member.domain.exception

import hello.kotlinlogintemplate.global.exception.error.BusinessException
import hello.kotlinlogintemplate.global.exception.error.ErrorCode

sealed class AuthException {
    class AuthorizationFailException(email: String) : BusinessException(ErrorCode.AUTHORIZATION_FAIL, email)
    class RefreshInvalidException : BusinessException(ErrorCode.REFRESH_TOKEN_INVALID)
}
