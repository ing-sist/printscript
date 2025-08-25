package builders

import AssignmentNode
import AstNode
import IdentifierNode
import Token

class AssignmentBuilder : AstBuilder {
    override fun build(tokens: List<Token>): AstNode {
        val identifier = IdentifierNode(tokens[0], tokens[0].lexeme)
        // La expresión son todos los tokens entre el '=' y el ';'
        val expressionTokens = tokens.subList(2, tokens.size - 1)
        val expression = ExpressionBuilder().build(expressionTokens)
        return AssignmentNode(identifier, expression)
    }
}
