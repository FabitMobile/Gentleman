package ru.fabit.gentleman

@JvmInline
value class Manner(val value: Int) {
    override fun toString(): String {
        return when (this) {
            usual -> "usual"
            gentle -> "gentle"
            rude -> "rude"
            else -> super.toString()
        }
    }
}

/**
 * Спрашиваем разрешение и, если нужно, показываем rationale экран
 */
val usual = Manner(0)

/**
 * Сначала показываем rationale экран
 */
val gentle = Manner(1)

/**
 * Только спрашиваем разрешение
 */
val rude = Manner(2)