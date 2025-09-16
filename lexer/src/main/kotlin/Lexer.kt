import java.io.Reader

class Lexer(
    private val reader: Reader,
    tokenRule: TokenRule,
) {
    private val tokenMatcher = TokenMatcher(tokenRule)
    private val buffer = StringBuilder()
    private var currentLine = 1
    private var currentColumn = 1
    private var isEndOfFile = false

    // Buffer para leer el archivo en bloques y mejorar la eficiencia
    private val readBuffer = CharArray(1024)

    /**
     * Devuelve el siguiente token significativo del stream, ignorando automáticamente
     * los espacios en blanco. Devuelve un token EOF si se acaba el input.
     * Esta función ahora tiene un único punto de salida, solucionando el error del linter.
     */
    fun nextToken(readSpace: Boolean): Token {
        while (true) {
            val token = produceNextToken()
            if (!readSpace) {
                if (token.type is TokenType.Space) {
                    continue
                }
            }
            return token
        }
    }

    /**
     * Función auxiliar privada que extrae el siguiente token del buffer,
     * sea del tipo que sea (incluyendo espacios en blanco).
     */
    private fun produceNextToken(): Token {
        if (buffer.isEmpty()) {
            fillBuffer()
            // Si después de intentar llenar el buffer, este sigue vacío,
            // significa que hemos llegado al final del archivo.
            if (buffer.isEmpty()) {
                return createToken("EOF", TokenType.EOF)
            }
        }

        // Delegamos al TokenMatcher y manejamos el resultado.
        // Usamos una expresión 'when' para devolver el valor directamente.
        return when (val matchResult = tokenMatcher.findNextToken(buffer.toString(), currentLine, currentColumn)) {
            is Result.Success -> {
                val token = matchResult.value
                consumeFromBuffer(token.lexeme)
                token
            }
            is Result.Failure -> {
                // Si no se reconoce ningún token, consumimos un solo carácter como error
                // para evitar bucles infinitos y poder seguir analizando.
                val invalidChar = buffer.first()
                consumeFromBuffer(invalidChar.toString())
                createToken(matchResult.toString(), TokenType.ERROR)
            }
        }
    }

    private fun consumeFromBuffer(lexeme: String) {
        val newlines = lexeme.count { it == '\n' }
        if (newlines > 0) {
            currentLine += newlines
            currentColumn = lexeme.length - lexeme.lastIndexOf('\n')
        } else {
            currentColumn += lexeme.length
        }
        buffer.delete(0, lexeme.length)
    }

    /**
     * Llena el buffer interno leyendo un bloque de caracteres del Reader.
     * Esto es mucho más eficiente que leer carácter por carácter.
     */
    private fun fillBuffer() {
        if (isEndOfFile) return

        val charsRead = reader.read(readBuffer)

        if (charsRead != -1) {
            buffer.append(readBuffer, 0, charsRead)
        } else {
            isEndOfFile = true
            reader.close()
        }
    }

    private fun createToken(
        lexeme: String,
        tokenType: TokenType,
    ): Token = Token(tokenType, lexeme, Location(currentLine, currentColumn, currentColumn + lexeme.length - 1))
}
