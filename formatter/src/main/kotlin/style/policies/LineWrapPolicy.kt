package style.policies

sealed class LineWrapPolicy {
    data object Off : LineWrapPolicy()

    data class Soft(
        val limit: Int,
    ) : LineWrapPolicy() // Soft wrapping at the specified character limit

    data class Hard(
        val limit: Int,
    ) : LineWrapPolicy()
}
