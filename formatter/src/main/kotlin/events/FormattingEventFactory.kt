package events

import Token
import TokenType

private val CONST_EVENTS: Map<TokenType, FormattingEvent> =
    mapOf(
        TokenType.Semicolon to Semicolon,
        TokenType.Comma to Comma,
        TokenType.LeftBrace to LeftBrace,
        TokenType.RightBrace to RightBrace,
        TokenType.Colon to Colon,
        TokenType.LeftParen to LeftParen,
        TokenType.RightParen to RightParen,
        TokenType.Newline to BlankLine,
        TokenType.Indent to Indent,
        TokenType.Dedent to Dedent,
        TokenType.Space to Space,
    )

private val OP_TEXT: Map<TokenType, String> =
    mapOf(
        TokenType.Assignment to "=",
        TokenType.Plus to "+",
        TokenType.Minus to "-",
        TokenType.Multiply to "*",
        TokenType.Divide to "/",
        TokenType.Equals to "==",
        TokenType.NotEquals to "!=",
        TokenType.LessThan to "<",
        TokenType.LessThanOrEqual to "<=",
        TokenType.GreaterThan to ">",
        TokenType.GreaterThanOrEqual to ">=",
    )

fun mapToken(token: Token): FormattingEvent =
    CONST_EVENTS[token.type]
        ?: OP_TEXT[token.type]?.let { Operator(it) }
        ?: when (token.type) {
            TokenType.Identifier -> Identifier(token.lexeme)
            TokenType.StringLiteral -> Literal(token.lexeme)
            TokenType.NumberLiteral -> Literal(token.lexeme)
            TokenType.Comment -> Comment(token.lexeme)
            TokenType.VariableDeclaration -> Keyword("let")
            TokenType.StringType -> Keyword("string")
            TokenType.NumberType -> Keyword("number")
            else -> Keyword(token.lexeme)
        }
