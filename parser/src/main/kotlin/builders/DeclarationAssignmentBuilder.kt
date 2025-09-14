package builders
import AstNode
import DeclarationAssignmentNode
import DeclarationNode
import Token

class DeclarationAssignmentBuilder : AstBuilder {
    override fun build(tokens: List<Token>): AstNode {
        val declaration = DeclarationBuilder().build(tokens.subList(0, 4))
        // La expresión son todos los tokens después del '=' y antes del ';'
        val expressionTokens = tokens.subList(5, tokens.size - 1)
        val expression = ExpressionBuilder().build(expressionTokens)
        return DeclarationAssignmentNode(declaration as DeclarationNode, expression)
    }
}
