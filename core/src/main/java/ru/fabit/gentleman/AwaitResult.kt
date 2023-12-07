package ru.fabit.gentleman

data class AwaitResult(
    val granted: List<String> = listOf(),
    val denied: List<String> = listOf()
)