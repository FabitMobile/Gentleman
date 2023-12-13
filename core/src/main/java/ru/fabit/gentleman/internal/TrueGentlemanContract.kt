package ru.fabit.gentleman.internal

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import ru.fabit.gentleman.Gentleman

abstract class TrueGentlemanContract<Input>(
    private val appearanceClass: Class<*>,
    private val layoutResId: Int
) : ActivityResultContract<Input, ContractResult>() {

    abstract fun Intent.putExtra(input: Input)

    override fun createIntent(context: Context, input: Input): Intent {
        return Intent(context, appearanceClass).apply {
            putExtra(input)
            putExtra(Gentleman.LAYOUT_RES_ID, layoutResId)
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): ContractResult {
        if (resultCode != Gentleman.RESULT_CODE || intent == null)
            return ContractResult.NEGATIVE
        val result =
            intent.getStringExtra(Gentleman.PERMISSIONS_RESULT) ?: ContractResult.NEGATIVE.name
        return ContractResult.valueOf(result)
    }
}