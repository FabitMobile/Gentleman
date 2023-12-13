package ru.fabit.gentleman.appearance

import ru.fabit.gentleman.R

class Tuxedo(
    override val rationaleLayoutResId: Int = R.layout.tuxedo,
    override val settingLayoutResId: Int = R.layout.inner_chamber,
    override val appearanceClass: Class<out Appearance> = Appearance::class.java,
    reparation: Preparation,
) : GentlemanAppearance(reparation)