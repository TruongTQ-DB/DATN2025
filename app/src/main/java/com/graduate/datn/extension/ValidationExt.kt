package com.graduate.datn.extension

import java.util.regex.Pattern

internal const val USER_NAME_REGEX = "^[0-9a-zA-Z]+$"
internal const val PASS_WORD_REGEX = "^[0-9a-zA-Z]+$"
internal const val PASS_WORD_UPPER = "^[0-9a-z]+$"
internal const val PASS_WORD_NUM = "^[a-zA-Z]+$"
internal const val PASS_WORD_REGEX_SPECIAL_CHARACTER = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{6,256}$"

internal const val PHONE_REGISTER_REGEX = "^\\d{3}-\\d{3}-\\d{3}\$"
internal const val PHONE_REGISTER_REGEX_DOT = "^\\d{3}[.]\\d{3}[.]\\d{3}\$"
internal const val PHONE_REGISTER_REGEX_SPACE = "^\\d{3}[ ]\\d{3}[ ]\\d{3}\$"
internal val START_PHONE_REGISTER_REGEX = listOf("0", "84", "+84", "(+84)", "(84)", "+(84)")

internal const val PASSWORD_LENGTH_VALIDATION = 6
internal const val PASSWORD_MAX_LENGTH_VALIDATION = 50
internal const val USER_NAME_MAX_LENGTH_VALIDATION = 100
internal const val NAME_MAX_LENGTH_VALIDATION = 255

fun String.isEmailValid(): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}
fun String.isPhoneValid(): Boolean {
    return android.util.Patterns.PHONE.matcher(this).matches()
}

fun String.isUserNameValid(): Boolean {
    return Pattern.compile(USER_NAME_REGEX).matcher(this).matches()
}

fun String.isPassWordValid(): Boolean {
    return Pattern.compile(PASS_WORD_REGEX).matcher(this).matches()
}

fun String.isPassWordUpper(): Boolean {
    return Pattern.compile(PASS_WORD_UPPER).matcher(this).matches()
}

fun String.isPassWordNumber(): Boolean {
    return Pattern.compile(PASS_WORD_NUM).matcher(this).matches()
}

fun String.isPassWordValidRegexSpecialChar(): Boolean {
    return Pattern.compile(PASS_WORD_REGEX_SPECIAL_CHARACTER).matcher(this).matches()
}

fun String.validateHealthInsurance() : Boolean {
    val isValid = if (Pattern.compile("^\\d{10}\$").matcher(this).matches()){
        Pattern.compile("^\\d{10}\$").matcher(this).matches()
    }else{
        Pattern.compile("^[A-Z]+$").matcher( this.substring(0, 2)).matches() && Pattern.compile("^\\d{13}\$").matcher(this.substring(2)).matches()
    }
    return isValid
}

fun String.validateIdentityCard() : Boolean {
    val checkStartNumber = this.startsWith("0")
    val isValid = Pattern.compile("^\\d{11}\$").matcher(this.substring(1)).matches()
    return checkStartNumber && isValid
}

fun String.validatePhoneInRegister() : Boolean {
    val checkStartNumber = START_PHONE_REGISTER_REGEX.filter {
        this.startsWith(it)
    }
    val start = checkStartNumber.lastOrNull() ?: ""
    val phoneAfterRemoveStart = this.substring(start.length)
    val isValid = if(Pattern.compile("^\\d{9}\$").matcher(phoneAfterRemoveStart).matches()){
        Pattern.compile("^\\d{9}\$").matcher(phoneAfterRemoveStart).matches()
    } else {
        Pattern.compile(PHONE_REGISTER_REGEX).matcher(phoneAfterRemoveStart).matches()
                || Pattern.compile(PHONE_REGISTER_REGEX_DOT).matcher(phoneAfterRemoveStart).matches()
                || Pattern.compile(PHONE_REGISTER_REGEX_SPACE).matcher(phoneAfterRemoveStart).matches()
    }
    return checkStartNumber.isNotEmpty() && isValid
}

fun String.checkHasContainAlphabetCharacter() : Boolean{
    return (Pattern.matches(".*[a-zA-Z]+.*", this))
}

fun String.checkHasOnlyNumber() : Boolean{
    return (Pattern.matches(".*[0-9]+.*", this))
}