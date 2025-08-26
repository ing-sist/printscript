package events

import Token
import TokenType

class FormattingEventFactory {
    fun mapToken(t: Token): FormattingEvent {
        val typeToEvent =
            mapOf(
                TokenType.Semicolon to Semicolon,
                TokenType.Comma to Comma,
                TokenType.LeftBrace to OpenBrace,
                TokenType.RightBrace to CloseBrace,
                TokenType.Colon to Operator(":"),
                TokenType.LeftParen to OpenParen,
                TokenType.RightParen to CloseParen,
                TokenType.Assignment to Operator("="),
                TokenType.Plus to Operator("+"),
                TokenType.Minus to Operator("-"),
                TokenType.Multiply to Operator("*"),
                TokenType.Divide to Operator("/"),
                TokenType.Equals to Operator("=="),
                TokenType.NotEquals to Operator("!="),
                TokenType.LessThan to Operator("<"),
                TokenType.LessThanOrEqual to Operator("<="),
                TokenType.GreaterThan to Operator(">"),
                TokenType.GreaterThanOrEqual to Operator(">="),
                TokenType.Identifier to Identifier(t.lexeme),
                TokenType.StringLiteral to Literal(t.lexeme),
                TokenType.NumberLiteral to Literal(t.lexeme),
                TokenType.VariableDeclaration to Keyword("let"),
                TokenType.StringType to Keyword("string"),
                TokenType.NumberType to Keyword("number"),
                TokenType.EOF to Eof,
            )
        return typeToEvent[t.type] ?: Keyword(t.lexeme) // Default to Keyword for unhandled types
    }
}
// agregar caso de fallo donde no encuentra el tipo en el mapa, usar result
//  TokenType.FunctionCall -> TODO()
