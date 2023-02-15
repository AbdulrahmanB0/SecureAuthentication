package com.practise.domain.model

object RegexPatterns {

    val EMAIL_ADDRESS = ("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
            "\\@" +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
            "(" +
            "\\." +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
            ")+").toRegex()


    val VALID_USERNAME = "(\\S+){2,20}".toRegex()

    val SAFE_PASSWORD = "((?=.*[0-9])(?=.*[A-Z])(?=.*[a-z])(?=.*\\W)){8,}".toRegex() //TODO: Not working properly, fix it.



}