package parser.dsl

import Location
import Token
import TokenType
import parser.Parser
import validators.DefaultValidatorsProvider

/**
 * DSL para crear tests de parser más legibles y mantenibles.
 * Permite escribir código como texto y automáticamente genera los tokens correspondientes.
 */
class TestDSL {
    private val tokens = mutableListOf<Token>()
    private var currentLine = 1
    private var currentColumn = 1

    /**
     * Construye tokens a partir de código fuente en texto plano.
     * Ejemplo: parseCode("let x: number = 42;")
     */
    fun parseCode(code: String): TestDSL {
        val words = tokenizeCode(code)
        words.forEach { word ->
            val token = createTokenFromWord(word)
            tokens.add(token)
            updatePosition(word)
        }
        return this
    }

    /**
     * Ejecuta el parser y devuelve el resultado.
     */
    fun parse() = Parser(DefaultValidatorsProvider()).parse(tokens)

    /**
     * Verifica que el parsing sea exitoso.
     */
    fun shouldParseSuccessfully(): ParseResult {
        val result = parse()
        return ParseResult(result, expectingSuccess = true)
    }

    /**
     * Verifica que el parsing falle.
     */
    fun shouldFailToParse(): ParseResult {
        val result = parse()
        return ParseResult(result, expectingSuccess = false)
    }

    private fun tokenizeCode(code: String): List<String> {
        val tokens = mutableListOf<String>()
        var i = 0
        val chars = code.toCharArray()

        while (i < chars.size) {
            i =
                when {
                    chars[i].isWhitespace() -> handleWhitespace(chars, i)
                    chars[i] == '"' -> handleStringLiteral(chars, i, code, tokens)
                    chars[i].isLetter() || chars[i] == '_' -> handleIdentifier(chars, i, code, tokens)
                    chars[i].isDigit() -> handleNumber(chars, i, code, tokens)
                    else -> handleSingleCharToken(chars, i, tokens)
                }
        }
        return tokens
    }

    private fun handleWhitespace(
        chars: CharArray,
        startIndex: Int,
    ): Int {
        var i = startIndex
        if (chars[i] == '\n') {
            currentLine++
            currentColumn = 1
        } else {
            currentColumn++
        }
        return i + 1
    }

    private fun handleStringLiteral(
        chars: CharArray,
        startIndex: Int,
        code: String,
        tokens: MutableList<String>,
    ): Int {
        val start = startIndex
        var i = startIndex + 1 // skip opening quote
        while (i < chars.size && chars[i] != '"') i++
        i++ // skip closing quote
        tokens.add(code.substring(start, i))
        return i
    }

    private fun handleIdentifier(
        chars: CharArray,
        startIndex: Int,
        code: String,
        tokens: MutableList<String>,
    ): Int {
        val start = startIndex
        var i = startIndex
        while (i < chars.size && (chars[i].isLetterOrDigit() || chars[i] == '_')) i++
        tokens.add(code.substring(start, i))
        return i
    }

    private fun handleNumber(
        chars: CharArray,
        startIndex: Int,
        code: String,
        tokens: MutableList<String>,
    ): Int {
        val start = startIndex
        var i = startIndex
        while (i < chars.size && (chars[i].isDigit() || chars[i] == '.')) i++
        tokens.add(code.substring(start, i))
        return i
    }

    private fun handleSingleCharToken(
        chars: CharArray,
        index: Int,
        tokens: MutableList<String>,
    ): Int {
        tokens.add(chars[index].toString())
        return index + 1
    }

    private fun createTokenFromWord(word: String): Token {
        val tokenType = mapWordToTokenType(word)
        val location = Location(currentLine, currentColumn, currentColumn + word.length - 1)
        return Token(tokenType, word, location)
    }

    private fun mapWordToTokenType(word: String): TokenType =
        getKeywordTokenType(word) ?: getOperatorTokenType(word) ?: classifyComplexToken(word)

    private fun getKeywordTokenType(word: String): TokenType? =
        when (word) {
            "let" -> TokenType.VariableDeclaration
            "number" -> TokenType.NumberType
            "string" -> TokenType.StringType
            "println" -> TokenType.FunctionCall
            else -> null
        }

    private fun getOperatorTokenType(word: String): TokenType? =
        when (word) {
            ":" -> TokenType.Colon
            ";" -> TokenType.Semicolon
            "=" -> TokenType.Assignment
            "+" -> TokenType.Plus
            "-" -> TokenType.Minus
            "*" -> TokenType.Multiply
            "/" -> TokenType.Divide
            "(" -> TokenType.LeftParen
            ")" -> TokenType.RightParen
            else -> null
        }

    private fun classifyComplexToken(word: String): TokenType =
        when {
            word.startsWith('"') && word.endsWith('"') -> TokenType.StringLiteral
            word.matches(Regex("""\d+(\.\d+)?""")) -> TokenType.NumberLiteral
            word.matches(Regex("""[a-zA-Z_][a-zA-Z0-9_]*""")) -> TokenType.Identifier
            else -> throw IllegalArgumentException("Token desconocido: $word")
        }

    private fun updatePosition(word: String) {
        currentColumn += word.length
    }
}

/**
 * Función de extensión para crear tests más fluidos.
 */
fun testCode(code: String) = TestDSL().parseCode(code)

/**
 * Función para tests con múltiples líneas de código.
 */
fun testProgram(vararg lines: String) = TestDSL().parseCode(lines.joinToString("\n"))
