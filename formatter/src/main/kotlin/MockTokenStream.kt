private fun eof() = Token(TokenType.EOF, "EOF", Location(-1, -1, -1))

class MockTokenStream(
    tokens: List<Token>,
) : TokenStream {
    private val data = tokens + eof()
    private var i = 0

    override fun peek(k: Int): Token = data.getOrNull(i + k) ?: eof()

    override fun consume(): Token = data.getOrNull(i++) ?: eof()
}
