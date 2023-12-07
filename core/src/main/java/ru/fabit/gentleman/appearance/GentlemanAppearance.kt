package ru.fabit.gentleman.appearance

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import ru.fabit.gentleman.Gentleman
import ru.fabit.gentleman.Gentleman.Companion.PERMISSION_KEY
import ru.fabit.gentleman.Gentleman.Companion.PERMISSION_RESULT
import ru.fabit.gentleman.Gentleman.Companion.RESULT_CODE
import ru.fabit.gentleman.GentlemanSet
import ru.fabit.gentleman.R
import ru.fabit.gentleman.internal.RationalResult
import ru.fabit.gentleman.internal.log

typealias Preparation = GentlemanSet.() -> Unit

abstract class GentlemanAppearance(
    private val preparation: Preparation? = null
) : AppCompatActivity() {
    protected abstract val layoutResId: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutResId)

        findViewById<View>(R.id.button_positive)?.setOnClickListener {
            finishWithResult(RationalResult.REQUEST_PERMISSIONS)
        } ?: log("Positive button not found")
        findViewById<View>(R.id.button_negative)?.setOnClickListener {
            finishWithResult(RationalResult.DENIED)
        } ?: log("Negative button not found")
    }

    private fun finishWithResult(result: RationalResult) {
        log("Rationale activity finished with result: $result")
        Intent().apply {
            putExtra(PERMISSION_RESULT, result.name)
            setResult(RESULT_CODE, this)
        }
        finish()
    }

    protected fun permissions(): Array<out String> =
        intent.extras?.getStringArray(PERMISSION_KEY) ?: arrayOf()

    operator fun contains(gentleman: Gentleman.Companion): Boolean {
        val set = GentlemanSet()
        preparation?.invoke(set)

        return gentleman.askForPermissions(set, this::class.java)
    }
}