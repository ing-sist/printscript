package builders.expresionHelpers

import AstNode
import IdentifierNode
import LiteralNode
import Token
import TokenType

class AstNodeFactory {
    fun createFromToken(token: Token): AstNode =
        when (token.type) {
            is TokenType.NumberLiteral -> LiteralNode(token)
            is TokenType.StringLiteral -> LiteralNode(token)
            is TokenType.Identifier -> IdentifierNode(token, token.lexeme)
            else -> throw IllegalArgumentException("Cannot create AstNode from token type: ${token.type}")
        }
}
