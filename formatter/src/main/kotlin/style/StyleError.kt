package style

sealed class StyleError {
    data class IndentSizeError(
        val reason: String,
    ) : StyleError()

    data class LineWrapError(
        val reason: String,
    ) : StyleError()

    data class BlankLinesError(
        val reason: String,
    ) : StyleError()
}
