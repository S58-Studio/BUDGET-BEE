package com.oneSaver.legacy.utils

fun String?.isNotNullOrBlank(): Boolean {
    return this != null && this.isNotBlank()
}
