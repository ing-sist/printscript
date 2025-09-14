package builders.expresionHelpers

import AstNode
import IdentifierNode
import LiteralNode
import Token
import TokenType

class AstNodeFactory {
    fun createFromToken(token: Token): AstNode =
        when (token.type) {
            TokenType.NumberLiteral -> LiteralNode(token)
            TokenType.StringLiteral -> LiteralNode(token)
            TokenType.BooleanLiteral -> LiteralNode(token) // Support for PrintScript 1.1 boolean literals
            TokenType.Identifier -> IdentifierNode(token, token.lexeme)
            else -> throw IllegalArgumentException(
                "Cannot create AstNode from token type: ${token.type} with value: '${token.lexeme}'",
            )
        }
}
