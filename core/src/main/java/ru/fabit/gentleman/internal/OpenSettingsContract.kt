package ru.fabit.gentleman.internal

import android.content.Intent
import ru.fabit.gentleman.appearance.Appearance

internal class OpenSettingsContract(
    layoutResId: Int,
    appearanceClass: Class<out Appearance>
) : TrueGentlemanContract<Any?>(appearanceClass, layoutResId) {
    override fun Intent.putExtra(input: Any?) {}
}
