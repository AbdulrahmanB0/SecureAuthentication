package com.practise.core
fun String.containsWhitespace() = toCharArray().any(Char::isWhitespace)
