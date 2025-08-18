sealed class LexError {
    data class SyntaxError(val reason: String) : LexError()
    data class InvalidVersion(val reason: String) : LexError()
}