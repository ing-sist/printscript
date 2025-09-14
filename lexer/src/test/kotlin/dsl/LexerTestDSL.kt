package dsl

import Lexer
import Result
import RuleGenerator
import Token
import TokenType
import java.io.StringReader

/**
 * DSL para crear tests de lexer más legibles y mantenibles.
 * Actualizado para usar el nuevo RuleGenerator basado en versiones.
 */
class LexerTestDSL {
    /**
     * Tokeniza código fuente usando la implementación actual del lexer.
     */
    fun tokenize(code: String): LexerResult =
        try {
            val tokenRule = RuleGenerator.createDefaultTokenRule()
            val reader = StringReader(code)
            val lexer = Lexer(reader, tokenRule)

            // Collect all tokens until EOF
            val tokens = mutableListOf<Token>()
            do {
                val token = lexer.nextToken()
                tokens.add(token)
            } while (token.type != TokenType.EOF)

            val result = Result.Success(tokens)
            LexerResult(result, code)
        } catch (e: Exception) {
            val result = Result.Failure(e.message ?: "Unknown lexer error")
            LexerResult(result, code)
        }

    /**
     * Tokeniza código fuente usando una versión específica de PrintScript.
     */
    fun tokenizeWithVersion(
        code: String,
        version: String,
    ): LexerResult =
        try {
            val tokenRule = RuleGenerator.createTokenRule(version)
            val reader = StringReader(code)
            val lexer = Lexer(reader, tokenRule)

            // Collect all tokens until EOF
            val tokens = mutableListOf<Token>()
            do {
                val token = lexer.nextToken()
                tokens.add(token)
            } while (token.type != TokenType.EOF)

            val result = Result.Success(tokens)
            LexerResult(result, code)
        } catch (e: Exception) {
            val result = Result.Failure(e.message ?: "Unknown lexer error")
            LexerResult(result, code)
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
     * Verifica el número total de tokens (incluyendo EOF).
     */
    fun withTokenCount(expectedCount: Int): TokenVerifier {
        require(tokens.size == expectedCount) {
            "Se esperaban $expectedCount tokens pero se obtuvieron ${tokens.size} para código: '$originalCode'"
        }
        return this
    }

    /**
     * Verifica el número de tokens excluyendo el EOF.
     */
    fun withTokenCountExcludingEOF(expectedCount: Int): TokenVerifier {
        val nonEofTokens = tokens.filter { it.type != TokenType.EOF }
        require(nonEofTokens.size == expectedCount) {
            "Se esperaban $expectedCount tokens (sin EOF) pero se obtuvieron " +
                "${nonEofTokens.size} para código: '$originalCode'"
        }
        return this
    }

    /**
     * Verifica que los tokens coincidan exactamente (incluyendo EOF).
     */
    fun withTokens(vararg expectedTypes: TokenType): TokenVerifier {
        require(tokens.size == expectedTypes.size) {
            "Se esperaban ${expectedTypes.size} tokens pero se obtuvieron ${tokens.size}"
        }

        expectedTypes.forEachIndexed { index, expectedType ->
            require(tokens[index].type == expectedType) {
                "Token en posición $index: se esperaba $expectedType pero fue " +
                    "${tokens[index].type} (lexema: '${tokens[index].lexeme}')"
            }
        }
        return this
    }

    /**
     * Verifica que los tokens coincidan exactamente (excluyendo EOF).
     */
    fun withTokensExcludingEOF(vararg expectedTypes: TokenType): TokenVerifier {
        val nonEofTokens = tokens.filter { it.type != TokenType.EOF }
        require(nonEofTokens.size == expectedTypes.size) {
            "Se esperaban ${expectedTypes.size} tokens pero se obtuvieron ${nonEofTokens.size}"
        }

        expectedTypes.forEachIndexed { index, expectedType ->
            require(nonEofTokens[index].type == expectedType) {
                "Token en posición $index: se esperaba $expectedType pero fue " +
                    "${nonEofTokens[index].type} (lexema: '${nonEofTokens[index].lexeme}')"
            }
        }
        return this
    }

    /**
     * Verifica un token específico en una posición determinada.
     */
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

    /**
     * Verifica que los tokens contengan tipos específicos.
     */
    fun containingTypes(vararg types: TokenType): TokenVerifier {
        val actualTypes = tokens.map { it.type }
        types.forEach { expectedType ->
            require(actualTypes.contains(expectedType)) {
                "Se esperaba que los tokens contuvieran $expectedType pero no se encontró. Tipos actuales: $actualTypes"
            }
        }
        return this
    }

    /**
     * Verifica que los lexemas coincidan exactamente.
     */
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

    /**
     * Verifica que NO contenga tipos específicos.
     */
    fun notContaining(vararg types: TokenType): TokenVerifier {
        val actualTypes = tokens.map { it.type }
        types.forEach { forbiddenType ->
            require(!actualTypes.contains(forbiddenType)) {
                "No se esperaba encontrar $forbiddenType pero fue encontrado. Tipos actuales: $actualTypes"
            }
        }
        return this
    }

    fun getTokens(): List<Token> = tokens

    fun getToken(index: Int): Token = tokens[index]

    fun getTokensExcludingEOF(): List<Token> = tokens.filter { it.type != TokenType.EOF }
}

/**
 * Función de extensión para crear tests más fluidas.
 */
fun lexCode(code: String) = LexerTestDSL().tokenize(code)

/**
 * Función para testear múltiples líneas de código.
 */
fun lexProgram(vararg lines: String) = LexerTestDSL().tokenize(lines.joinToString("\n"))

/**
 * Función para testear con una versión específica de PrintScript.
 */
fun lexCodeWithVersion(
    code: String,
    version: String,
) = LexerTestDSL().tokenizeWithVersion(code, version)

/**
 * Función para testear código de PrintScript 1.0.
 */
fun lexCode10(code: String) = lexCodeWithVersion(code, "1.0")

/**
 * Función para testear código de PrintScript 1.1.
 */
fun lexCode11(code: String) = lexCodeWithVersion(code, "1.1")

/**
 * Función para testear múltiples líneas con versión específica.
 */
fun lexProgramWithVersion(
    version: String,
    vararg lines: String,
) = LexerTestDSL().tokenizeWithVersion(lines.joinToString("\n"), version)
