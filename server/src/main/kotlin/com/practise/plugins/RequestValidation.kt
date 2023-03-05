package com.practise.plugins

import com.practise.core.isEmptyOrBlank
import com.practise.domain.model.MessagesResource
import com.practise.domain.model.RegexPatterns
import com.practise.domain.model.user.RegularUser
import com.practise.domain.model.user.UserUpdate
import domain.model.TokenId
import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*

fun Application.configureRequestValidation() {
    install(RequestValidation) {
        validateTokenId()
        validateUserRegistrationInfo()
        validateUserUpdate()
    }
}

private fun RequestValidationConfig.validateUserUpdate() {
    validate<UserUpdate> {
        val errors = mutableListOf<String>()
        if (it.firstName.isEmptyOrBlank()) {
            errors += MessagesResource.UPDATE_FIRST_NAME_ERROR.message
        }
        if (it.lastName.isEmptyOrBlank()) {
            errors += MessagesResource.UPDATE_LAST_NAME_ERROR.message
        }

        if (errors.isEmpty()) {
            ValidationResult.Valid
        } else {
            ValidationResult.Invalid(errors)
        }
    }
}

private fun RequestValidationConfig.validateTokenId() {
    validate<TokenId> { token ->
        if (token.value.isEmptyOrBlank()) {
            ValidationResult.Invalid(MessagesResource.TOKEN_FORMAT_ERROR.message)
        } else {
            ValidationResult.Valid
        }
    }
}
private fun RequestValidationConfig.validateUserRegistrationInfo() {
    validate<RegularUser> {
        val errors = mutableListOf<String>()

        if (!it.emailAddress.value.matches(RegexPatterns.EMAIL_ADDRESS)) {
            errors += "Email address format is not valid"
        }
        if (!it.username.matches(RegexPatterns.VALID_USERNAME)) {
            errors += "Username does not match valid username criteria"
        }

        if (errors.isEmpty()) {
            ValidationResult.Valid
        } else {
            ValidationResult.Invalid(errors)
        }
    }
}
