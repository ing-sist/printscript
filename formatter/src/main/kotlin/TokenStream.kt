private fun eof() = Token(TokenType.EOF, "EOF", Location(-1, -1, -1))

class TokenStream(
    tokens: List<Token>,
) {
    private val data = tokens + eof()
    private var i = 0

    fun peek(k: Int): Token = data.getOrNull(i + k) ?: eof()

    fun consume(): Token = data.getOrNull(i++) ?: eof()
}
