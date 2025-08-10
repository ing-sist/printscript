package org.example.lexer

import org.example.common.TokenType
import java.util.regex.Pattern

object LexerGenerator {

    fun createLexer(version: String): Lexer {
        return when (version) {
            "1.0" -> Lexer(getKeywords(), getRules())
            else -> throw IllegalArgumentException("Versión no soportada: $version")
        }
    }

    private fun getKeywords(): Map<String, TokenType> {
        return mapOf(
            "let" to TokenType.Let,
            "println" to TokenType.Println,
            "string" to TokenType.DataType.String,
            "number" to TokenType.DataType.Number
        )
    }

    private fun getRules(): Map<Pattern, (String) -> TokenType> {
        return mapOf(
            // String Literals with "" or ''
            Pattern.compile("^\"[^\"]*\"") to { lexeme: String ->
                TokenType.StringLiteral(lexeme.removeSurrounding("\""))
            },
            Pattern.compile("^'[^']*'") to { lexeme: String ->
                TokenType.StringLiteral(lexeme.removeSurrounding("'"))
            },

            // Number Literals
            Pattern.compile("^\\d+(\\.\\d+)?") to { lexeme: String ->
                TokenType.NumberLiteral(lexeme.toDouble())
            },

            // Identifiers
            Pattern.compile("^[a-zA-Z_][a-zA-Z0-9_]*") to { lexeme: String ->
                TokenType.Identifier(lexeme)
            },

            // Symbols
            Pattern.compile("^:") to { _: String -> TokenType.Colon },
            Pattern.compile("^;") to { _: String -> TokenType.Semicolon },
            Pattern.compile("^\\(") to { _: String -> TokenType.LeftParen },
            Pattern.compile("^\\)") to { _: String -> TokenType.RightParen },

            // Operators (multi-character first)
            Pattern.compile("^(==|!=|<=|>=|[=+\\-*/])") to { lexeme: String ->
                TokenType.Operator(lexeme)
            }
        )
    }
}