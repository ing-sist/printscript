package dsl

import Lexer
import LexerGenerator
import Result
import Token
import TokenType

/**
 * DSL para crear tests de lexer más legibles y mantenibles.
 * Actualizado para reflejar la implementación real del lexer.
 */
class LexerTestDSL {
    private val lexer: Lexer = createLexer()

    /**
     * Tokeniza código fuente y devuelve el resultado para verificaciones.
     */
    fun tokenize(code: String): LexerResult {
        val result = lexer.lex(code)
        return LexerResult(result, code)
    }

    private fun createLexer(): Lexer {
        // Usar el lexer por defecto que tiene todas las reglas correctas
        return LexerGenerator.createDefaultLexer()
    }
}

/**
 * Resultado de la tokenización con métodos de verificación fluidas.
 */
class LexerResult(
    private val result: Result<List<Token>, *>,
    private val originalCode: String,
) {
    fun shouldTokenizeSuccessfully(): TokenVerifier {
        require(result.isSuccess) {
            "Se esperaba tokenización exitosa pero falló: ${result.errorOrNull()}"
        }
        return TokenVerifier(result.getOrNull()!!, originalCode)
    }

    fun shouldFailToTokenize(): LexerResult {
        require(result.isFailure) {
            "Se esperaba que la tokenización fallara pero fue exitosa"
        }
        return this
    }

    fun withErrorMessage(expectedMessage: String): LexerResult {
        require(result.isFailure) {
            "Se esperaba error pero la tokenización fue exitosa"
        }
        val error = result.errorOrNull()!!
        require(error.toString().contains(expectedMessage)) {
            "Se esperaba error con mensaje '$expectedMessage' pero fue: '$error'"
        }
        return this
    }
}

/**
 * Verificador de tokens con métodos fluidos para validaciones.
 */
class TokenVerifier(
    private val tokens: List<Token>,
    private val originalCode: String,
) {
    /**
     * Verifica el número total de tokens (incluyendo EOF automático).
     */
    fun withTokenCount(expectedCount: Int): TokenVerifier {
        require(tokens.size == expectedCount) {
            "Se esperaban $expectedCount tokens pero se obtuvieron ${tokens.size} para código: '$originalCode'"
        }
        return this
    }

    /**
     * Verifica el número de tokens excluyendo el EOF automático.
     */
    fun withTokenCountExcludingEOF(expectedCount: Int): TokenVerifier {
        val nonEofTokens = tokens.filter { it.type != TokenType.EOF }
        require(nonEofTokens.size == expectedCount) {
            "Se esperaban $expectedCount tokens (sin EOF) " +
                "pero se obtuvieron ${nonEofTokens.size} para código: '$originalCode'"
        }
        return this
    }

    /**
     * Verifica que los tokens (incluyendo EOF) coincidan exactamente.
     */
    fun withTokens(vararg expectedTypes: TokenType): TokenVerifier {
        require(tokens.size == expectedTypes.size) {
            "Se esperaban ${expectedTypes.size} tokens pero se obtuvieron ${tokens.size}"
        }

        expectedTypes.forEachIndexed { index, expectedType ->
            require(tokens[index].type == expectedType) {
                "Token en posición $index: se esperaba $expectedType " +
                    "pero fue ${tokens[index].type} (lexema: '${tokens[index].lexeme}')"
            }
        }
        return this
    }

    /**
     * Verifica que los tokens (excluyendo EOF) coincidan exactamente.
     */
    fun withTokensExcludingEOF(vararg expectedTypes: TokenType): TokenVerifier {
        val nonEofTokens = tokens.filter { it.type != TokenType.EOF }
        require(nonEofTokens.size == expectedTypes.size) {
            "Se esperaban ${expectedTypes.size} tokens pero se obtuvieron ${nonEofTokens.size}"
        }

        expectedTypes.forEachIndexed { index, expectedType ->
            require(nonEofTokens[index].type == expectedType) {
                "Token en posición $index: se esperaba $expectedType " +
                    "pero fue ${nonEofTokens[index].type} (lexema: '${nonEofTokens[index].lexeme}')"
            }
        }
        return this
    }

    fun withTokenAt(
        index: Int,
        expectedType: TokenType,
        expectedLexeme: String? = null,
    ): TokenVerifier {
        require(index < tokens.size) {
            "Índice $index fuera de rango. Solo hay ${tokens.size} tokens"
        }

        val token = tokens[index]
        require(token.type == expectedType) {
            "Token en posición $index: se esperaba tipo $expectedType pero fue ${token.type}"
        }

        expectedLexeme?.let { lexeme ->
            require(token.lexeme == lexeme) {
                "Token en posición $index: se esperaba lexema '$lexeme' pero fue '${token.lexeme}'"
            }
        }
        return this
    }

    fun containingTypes(vararg types: TokenType): TokenVerifier {
        val actualTypes = tokens.map { it.type }
        types.forEach { expectedType ->
            require(actualTypes.contains(expectedType)) {
                "Se esperaba que los tokens contuvieran $expectedType pero no se encontró. Tipos actuales: $actualTypes"
            }
        }
        return this
    }

    fun withLexemes(vararg expectedLexemes: String): TokenVerifier {
        val nonEofTokens = tokens.filter { it.type != TokenType.EOF }
        require(nonEofTokens.size == expectedLexemes.size) {
            "Se esperaban ${expectedLexemes.size} lexemas pero se obtuvieron ${nonEofTokens.size} tokens"
        }

        expectedLexemes.forEachIndexed { index, expectedLexeme ->
            require(nonEofTokens[index].lexeme == expectedLexeme) {
                "Lexema en posición $index: se esperaba '$expectedLexeme' pero fue '${nonEofTokens[index].lexeme}'"
            }
        }
        return this
    }

    /**
     * Verifica que el último token sea EOF.
     */
    fun endsWithEOF(): TokenVerifier {
        require(tokens.isNotEmpty()) {
            "No hay tokens para verificar EOF"
        }
        require(tokens.last().type == TokenType.EOF) {
            "Se esperaba que el último token fuera EOF pero fue ${tokens.last().type}"
        }
        return this
    }

    fun getTokens(): List<Token> = tokens

    fun getToken(index: Int): Token = tokens[index]

    fun getTokensExcludingEOF(): List<Token> = tokens.filter { it.type != TokenType.EOF }
}

/**
 * Función de extensión para crear tests más fluidos.
 */
fun lexCode(code: String) = LexerTestDSL().tokenize(code)

/**
 * Función para testear múltiples líneas de código.
 */
fun lexProgram(vararg lines: String) = LexerTestDSL().tokenize(lines.joinToString("\n"))
