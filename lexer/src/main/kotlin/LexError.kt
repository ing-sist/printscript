sealed class LexError {
    data class SyntaxError(val reason: String) : LexError()
    data class UnexpectedToken(val line: Int, val column: Int, val preview: String) : LexError()
    data class InvalidCharacter(val line: Int, val column: Int, val character: Char) : LexError()
}