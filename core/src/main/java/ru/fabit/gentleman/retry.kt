package ru.fabit.gentleman

@JvmInline
value class Retry(val value: Int)

val none = Retry(0)
val once = Retry(1)