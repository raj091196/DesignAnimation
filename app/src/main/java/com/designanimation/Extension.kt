package com.designanimation

fun <T> T?.isNull(default: T): T {
    if (this == null) return default
    return this
}