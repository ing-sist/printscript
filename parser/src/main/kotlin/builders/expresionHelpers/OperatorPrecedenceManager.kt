package builders.expresionHelpers

import TokenType

class OperatorPrecedenceManager {
    private val precedence =
        mapOf(
            TokenType.Plus to 1,
            TokenType.Minus to 1,
            TokenType.Multiply to 2,
            TokenType.Divide to 2,
        )

    fun getPrecedence(tokenType: TokenType): Int = precedence[tokenType] ?: 0

    fun isOperator(tokenType: TokenType): Boolean = precedence.containsKey(tokenType)

    fun hasHigherOrEqualPrecedence(
        operator1: TokenType,
        operator2: TokenType,
    ): Boolean = getPrecedence(operator1) >= getPrecedence(operator2)
}
