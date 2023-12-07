package ru.fabit.gentleman

import android.content.Context

typealias Callback = (AwaitResult) -> Unit

class GentlemanSet {
    var context: Context? = null
        private set
    var permissions: List<String> = listOf()
        private set
    var callback: Callback? = null
        private set
    var retry: Retry = none
        private set

    infix fun with(context: Context) {
        this.context = context
    }

    fun ask(vararg permissions: String): Ask {
        this.permissions = permissions.toList()
        return Ask()
    }

    fun ask(permissions: List<String>): Ask {
        this.permissions = permissions
        return Ask()
    }

    infix fun await(callback: Callback?) {
        this.callback = callback
    }

    override fun toString(): String {
        return "GentlemanSet(target=$permissions)"
    }

    inner class Ask {
        infix fun retry(mode: Retry) {
            retry = mode
        }
    }
}
