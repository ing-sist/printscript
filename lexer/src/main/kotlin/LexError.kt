sealed class LexError {
    data class UnexpectedToken(val line: Int, val column: Int, val preview: String) : LexError()
}