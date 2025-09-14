package builders

import AstNode
import FunctionCallNode
import Token

class FunctionCallBuilder : AstBuilder {
    override fun build(tokens: List<Token>): AstNode {
        // La expresión son todos los tokens dentro de los paréntesis
        val expressionTokens = tokens.subList(2, tokens.size - 2)
        val expression = ExpressionBuilder().build(expressionTokens)
        return FunctionCallNode(
            functionName = tokens[0].lexeme,
            content = expression,
            isVoid = tokens[0].lexeme == "println",
        )
    }
}
