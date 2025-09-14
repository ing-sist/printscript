package builders

import AstNode
import DeclarationNode
import IdentifierNode
import Token

class DeclarationBuilder : AstBuilder {
    override fun build(tokens: List<Token>): AstNode {
        val isMutable = tokens[0].lexeme == "let"
        val identifier = IdentifierNode(tokens[1], tokens[1].lexeme)
        val type = tokens[3]
        return DeclarationNode(identifier, type, isMutable)
    }
}
