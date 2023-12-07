package ru.fabit.gentleman.internal

import android.util.Log
import ru.fabit.gentleman.Gentleman

internal fun log(text: String) {
    if (Gentleman.DEBUG)
        Log.d("Gentleman", text)
}