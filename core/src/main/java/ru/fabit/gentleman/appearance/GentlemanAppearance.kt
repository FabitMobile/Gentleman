package ru.fabit.gentleman.appearance

import ru.fabit.gentleman.Gentleman
import ru.fabit.gentleman.GentlemanSet

typealias Preparation = GentlemanSet.() -> Unit

abstract class GentlemanAppearance(
    private val preparation: Preparation
) {
    protected abstract val rationaleLayoutResId: Int

    protected abstract val settingLayoutResId: Int

    protected abstract val appearanceClass: Class<out Appearance>

    operator fun contains(gentleman: Gentleman.Companion): Boolean {
        val set = GentlemanSet()
        preparation(set)

        return gentleman.askForPermissions(
            params = set,
            appearanceClass = appearanceClass,
            rationaleLayoutResId = rationaleLayoutResId,
            settingLayoutResId = settingLayoutResId
        )
    }
}