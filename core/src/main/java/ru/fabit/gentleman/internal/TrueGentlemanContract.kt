package ru.fabit.gentleman.internal

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import ru.fabit.gentleman.Gentleman.Companion.PERMISSION_KEY
import ru.fabit.gentleman.Gentleman.Companion.PERMISSION_RESULT
import ru.fabit.gentleman.Gentleman.Companion.RESULT_CODE

internal class TrueGentlemanContract(
    private val appearanceClass: Class<*>
) : ActivityResultContract<Array<String>, RationalResult>() {

    override fun createIntent(context: Context, input: Array<String>): Intent {
        return Intent(context, appearanceClass).putExtra(PERMISSION_KEY, input)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): RationalResult {
        if (resultCode != RESULT_CODE || intent == null)
            return RationalResult.DENIED
        val result =
            intent.getStringExtra(PERMISSION_RESULT) ?: RationalResult.DENIED.name
        return RationalResult.valueOf(result)
    }
}