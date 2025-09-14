class LexerException(
    val token: Token,
) : RuntimeException("Error léxico en línea ${token.location.line}, columna ${token.location.startCol}: '${token.lexeme}'")

/**
 * Implementación de TokenProvider que envuelve al Lexer para añadir
 * un buffer de lookahead (la capacidad de espiar).
 *
 * @param lexer La instancia del Lexer de streaming que generará los tokens.
 */
class LexerTokenProvider(private val lexer: Lexer) : TokenProvider {
    private val lookahead = mutableListOf<Token>()
    private var eofReached = false

    override fun peek(k: Int): Token {
        while (lookahead.size <= k && !eofReached) {
            val next = lexer.nextToken()

            if (next.type is TokenType.ERROR) {
                throw LexerException(next)
            }

            lookahead.add(next)
            if (next.type is TokenType.EOF) {
                eofReached = true
            }
        }
        return lookahead.getOrElse(k) { lookahead.last() } // Devuelve EOF si se pasa
    }

    override fun consume(): Token {
        // Asegurarse de que el buffer tenga al menos un token para consumir
        if (lookahead.isEmpty()) {
            peek()
        }
        return lookahead.removeAt(0)
    }
}