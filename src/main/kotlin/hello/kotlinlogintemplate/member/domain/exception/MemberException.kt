package hello.kotlinlogintemplate.member.domain.exception

import hello.kotlinlogintemplate.global.exception.error.BusinessException
import hello.kotlinlogintemplate.global.exception.error.ErrorCode

sealed class MemberException {
    class DuplicateEmailException : BusinessException(ErrorCode.DUPLICATE_EMAIL)
    class NotExistMemberException : BusinessException(ErrorCode.NOT_EXIST_MEMBER)
}
