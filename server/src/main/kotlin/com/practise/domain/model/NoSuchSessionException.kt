package com.practise.domain.model

/**
 * This exception class is used to force the server consider the user as unauthorized.
 * Used to mark user as unauthorized in situations considers it as internal error
 */
class NoSuchSessionException(message: String): NoSuchElementException(message)