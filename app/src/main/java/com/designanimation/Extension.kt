package com.designanimation

import android.util.Log

fun <T> T?.isNull(default: T): T {
    if (this == null) return default
    return this
}

fun <T> List<T>?.isEmpty(function: (List<T>) -> Unit,function1: () -> Unit) {
    if (this == null || this.isEmpty()) {
        function1()
        return
    }
    function(this)
}

fun Any.d(message: String) {
    Log.d(this::class.java.simpleName, message)
}