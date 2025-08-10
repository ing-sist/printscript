package org.example.lexer

import org.example.common.Location
import org.example.common.Token
import org.example.common.TokenType
import java.util.regex.Matcher
import java.util.regex.Pattern

class Lexer(
    private val keywordRules: Map<String, TokenType>,
    private val generalRules: Map<Pattern, (String) -> TokenType>

) {
    fun lex(source: String): List<Token> {

        val tokens = mutableListOf<Token>()
        var remainingSource = source
        var currentLine = 1
        var currentColumn = 1

        while (remainingSource.isNotEmpty()) {
            val initialWhitespace = remainingSource.takeWhile { it.isWhitespace() }
            if (initialWhitespace.isNotEmpty()) {
                val newlines = initialWhitespace.count { it == '\n' }
                if (newlines > 0) {
                    currentLine += newlines
                    currentColumn = initialWhitespace.length - initialWhitespace.lastIndexOf('\n')
                } else {
                    currentColumn += initialWhitespace.length
                }
                remainingSource = remainingSource.substring(initialWhitespace.length)
                if (remainingSource.isEmpty()) break
            }
            var matchFound = false

            // Paso 1: Keywords y data types
            for ((kw, type) in keywordRules) {
                val regex = Regex("^\\b$kw\\b")
                val match = regex.find(remainingSource)
                if (match != null) {
                    val lexeme = match.value
                    tokens.add(Token(type, lexeme, Location(currentLine, currentColumn)))
                    currentColumn += lexeme.length
                    remainingSource = remainingSource.substring(lexeme.length)
                    matchFound = true
                    break
                }
            }
            if (matchFound) continue

            // Paso 2: Reglas generales (Map de Pattern)
            for ((pattern, tokenBuilder) in generalRules) {
                val matcher: Matcher = pattern.matcher(remainingSource)
                if (matcher.find() && matcher.start() == 0) {
                    val lexeme = matcher.group()
                    val tokenType = tokenBuilder(lexeme)
                    tokens.add(Token(tokenType, lexeme, Location(currentLine, currentColumn)))
                    currentColumn += lexeme.length
                    remainingSource = remainingSource.substring(lexeme.length)
                    matchFound = true
                    break
                }
            }
            if (!matchFound) {
                throw IllegalStateException(
                    "Error de sintaxis: Token inesperado en línea $currentLine, columna $currentColumn cerca de '${remainingSource.take(10)}...'"
                )
            }
        }
        tokens.add(Token(TokenType.EOF, "", Location(currentLine, currentColumn)))
        return tokens
    }
}
