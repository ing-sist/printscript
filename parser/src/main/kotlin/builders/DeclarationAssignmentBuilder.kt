package builders
import AstNode
import DeclarationAssignmentNode
import IdentifierNode
import Token

class DeclarationAssignmentBuilder : AstBuilder {
    override fun build(tokens: List<Token>): AstNode {
        val identifier = IdentifierNode(tokens[1], tokens[1].lexeme)
        val type = tokens[3]
        // La expresión son todos los tokens después del '=' y antes del ';'
        val expressionTokens = tokens.subList(5, tokens.size - 1)
        val expression = ExpressionBuilder().build(expressionTokens)
        return DeclarationAssignmentNode(identifier, type, expression)
    }
}
