package hello.kotlinlogintemplate.global.exception

import hello.kotlinlogintemplate.global.exception.error.ErrorCode
import org.springframework.http.HttpStatus
import org.springframework.util.MultiValueMap
import org.springframework.validation.BindingResult
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException


class ErrorResponse(
    var message: String,
    var status: Int,
    var errors: List<FieldCustomError>
) {

    companion object {
        fun of(code: ErrorCode, errors: List<FieldCustomError>) = ErrorResponse(code.message, code.status, errors)

        fun of(code: ErrorCode) = ErrorResponse(code.message, code.status, emptyList())

        fun of(code: ErrorCode, bindingResult: BindingResult) =
            ErrorResponse(code.message, code.status, FieldCustomError.of(bindingResult))

        fun of(e: MethodArgumentTypeMismatchException): ErrorResponse {
            val value = e.value.toString()
            val errors: List<FieldCustomError> = FieldCustomError.of(e.name, value, e.errorCode)
            val errorCode = ErrorCode.INVALID_TYPE_VALUE
            return ErrorResponse(errorCode.message, errorCode.status, errors)
        }

        fun of(errorMessage: String): ErrorResponse {
            val errors: List<FieldCustomError> = FieldCustomError.of("error", errorMessage, errorMessage)
            return ErrorResponse(errorMessage, HttpStatus.BAD_REQUEST.value(), errors)
        }
    }

    class FieldCustomError(
        var field: String,
        var value: String,
        var reason: String
    ) {

        companion object {
            fun of(bindingResult: BindingResult) = bindingResult.fieldErrors.stream()
                .map { error ->
                    FieldCustomError(
                        error.field,
                        error.rejectedValue.toString(),
                        error?.defaultMessage ?: ""
                    )
                }
                .toList()

            fun of(field: String, value: String, reason: String): List<FieldCustomError> {
                val fieldErrors: MutableList<FieldCustomError> = ArrayList()
                fieldErrors.add(FieldCustomError(field, value, reason))
                return fieldErrors
            }
        }
    }
}
