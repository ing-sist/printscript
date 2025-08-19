data class SourcePosition(val text: String, val line: Int, val column: Int) {
    fun hasRemaining(): Boolean = text.isNotEmpty()
}