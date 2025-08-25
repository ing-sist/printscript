package builders

import AstNode
import PrintlnNode
import Token

class PrintlnBuilder : AstBuilder {
    override fun build(tokens: List<Token>): AstNode {
        // La expresión son todos los tokens dentro de los paréntesis
        val expressionTokens = tokens.subList(2, tokens.size - 2)
        val expression = ExpressionBuilder().build(expressionTokens)
        return PrintlnNode(expression)
    }
}
