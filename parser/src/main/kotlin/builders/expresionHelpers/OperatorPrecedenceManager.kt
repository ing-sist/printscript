package builders.expresionHelpers

import TokenType

class OperatorPrecedenceManager {
    private val precedence =
        mapOf(
            TokenType.Operator.Plus to 1,
            TokenType.Operator.Minus to 1,
            TokenType.Operator.Multiply to 2,
            TokenType.Operator.Divide to 2,
        )

    fun getPrecedence(tokenType: TokenType): Int = precedence[tokenType] ?: 0

    fun isOperator(tokenType: TokenType): Boolean = precedence.containsKey(tokenType)

    fun hasHigherOrEqualPrecedence(
        operator1: TokenType,
        operator2: TokenType,
    ): Boolean = getPrecedence(operator1) >= getPrecedence(operator2)
}
