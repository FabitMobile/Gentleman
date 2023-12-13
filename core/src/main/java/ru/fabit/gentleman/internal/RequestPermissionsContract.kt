package ru.fabit.gentleman.internal

import android.content.Intent
import ru.fabit.gentleman.Gentleman.Companion.PERMISSIONS
import ru.fabit.gentleman.appearance.Appearance

internal class RequestPermissionsContract(
    layoutResId: Int,
    appearanceClass: Class<out Appearance>
) : TrueGentlemanContract<Array<String>>(appearanceClass, layoutResId) {

    override fun Intent.putExtra(input: Array<String>) {
        putExtra(PERMISSIONS, input)
    }
}