package viejos

data class CommandError(
    val message: String,
    val location: Location? = null,
    val cause: Throwable? = null,
)