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
    var manner: Manner = usual
        private set
    var openSettings: Boolean = false
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

    infix fun manner(mode: Manner) {
        manner = mode
    }

    infix fun Manner.manner(ask: Ask): Ask {
        return ask.manner(this)
    }

    override fun toString(): String {
        return "GentlemanSet(target=$permissions)"
    }

    inner class Ask {
        infix fun retry(mode: Retry): Ask {
            retry = mode
            return this
        }

        infix fun manner(mode: Manner): Ask {
            manner = mode
            return this
        }
    }

    inner class Suggest {
        infix fun goTo(goTo: GoTo) {
            openSettings = goTo == innerChamber
        }
    }

    val suggest = Suggest()
}
