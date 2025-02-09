package com.financeAndMoney.legacy.utils

fun String?.isNotNullOrBlank(): Boolean {
    return this != null && this.isNotBlank()
}
