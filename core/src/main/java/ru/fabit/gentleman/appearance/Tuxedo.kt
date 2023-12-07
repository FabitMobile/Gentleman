package ru.fabit.gentleman.appearance

import ru.fabit.gentleman.R

class Tuxedo(
    override val layoutResId: Int = R.layout.tuxedo,
    reparation: Preparation? = null
) : GentlemanAppearance(reparation)