package com.practise.core

fun String.isEmptyOrBlank() = isEmpty() || isBlank()

fun String.containsWhitespace() = toCharArray().any(Char::isWhitespace)