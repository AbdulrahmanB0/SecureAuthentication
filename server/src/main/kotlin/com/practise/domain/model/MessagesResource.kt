package com.practise.domain.model

enum class MessagesResource(val message: String) {
    SESSION_FAIL("Error in obtaining session info!"),
    USER_INFO_RETRIEVAL_SUCCESS("User info retrieved successfully"),
    USER_INFO_RETRIEVAL_FAILED("Failed to retrieve user info"),
    USER_INFO_UPDATE_SUCCESS("User information is updated successfully"),
    USER_INFO_UPDATE_FAILED("Failed to update user information"),
    USER_DELETE_SUCCESS("User account has been deleted successfully"),
    USER_DELETE_FAILED("Failed to delete user account"),
    UNAUTHORIZED("Not Authorized"),
    SIGNED_OUT("User has been signed out successfully"),
    TOKEN_FORMAT_ERROR("Token ID should not be blank or empty!"),
    TOKEN_VERIFICATION_FAILED("Failed to very Token ID"),
    UPDATE_FIRST_NAME_ERROR("First name should not be blank or empty!"),
    UPDATE_LAST_NAME_ERROR("Last name should not be blank or empty!"),
    USER_SIGN_IN_SUCCESS("User is successfully signed in"),
    USER_REGISTER_SUCCESS("User is registered successfully"),
    USER_REGISTER_FAIL("Failed to register user"),
    USER_LOGIN_SUCCESS("User is logged in successfully"),
    EMAIL_VERIFY_SUCCESS("User email is successfully verified"),
    EMAIL_VERIFY_FAIL("Failed to verify user email")
}