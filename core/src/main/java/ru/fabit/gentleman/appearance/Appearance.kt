package ru.fabit.gentleman.appearance

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import ru.fabit.gentleman.Gentleman
import ru.fabit.gentleman.R
import ru.fabit.gentleman.internal.ContractResult
import ru.fabit.gentleman.internal.log

open class Appearance : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutResId())

        findViewById<View>(R.id.button_positive)?.setOnClickListener {
            finishWithResult(ContractResult.POSITIVE)
        } ?: log("Positive button not found")
        findViewById<View>(R.id.button_negative)?.setOnClickListener {
            finishWithResult(ContractResult.NEGATIVE)
        } ?: log("Negative button not found")
    }

    private fun finishWithResult(result: ContractResult) {
        log("Rationale activity finished with result: $result")
        Intent().apply {
            putExtra(Gentleman.PERMISSIONS_RESULT, result.name)
            setResult(Gentleman.RESULT_CODE, this)
        }
        finish()
    }

    protected fun permissions(): Array<out String> =
        intent.extras?.getStringArray(Gentleman.PERMISSIONS) ?: arrayOf()

    protected fun layoutResId(): Int =
        intent.extras?.getInt(Gentleman.LAYOUT_RES_ID) ?: R.layout.tuxedo

}