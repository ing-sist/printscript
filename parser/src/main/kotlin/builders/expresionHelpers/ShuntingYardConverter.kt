package builders.expresionHelpers

import Token
import TokenType
import java.util.LinkedList
import java.util.Queue
import java.util.Stack

class ShuntingYardConverter(
    private val precedenceManager: OperatorPrecedenceManager,
) {
    fun convertToRPN(tokens: List<Token>): Queue<Any> {
        val outputQueue: Queue<Any> = LinkedList()
        val operatorStack: Stack<Token> = Stack()

        for (token in tokens) {
            when (token.type) {
                is TokenType.NumberLiteral,
                is TokenType.StringLiteral,
                is TokenType.Identifier,
                -> outputQueue.add(AstNodeFactory().createFromToken(token))

                is TokenType.LeftParen -> operatorStack.push(token)

                is TokenType.RightParen -> processRightParenthesis(operatorStack, outputQueue)

                else -> processOperator(token, operatorStack, outputQueue)
            }
        }

        while (operatorStack.isNotEmpty()) {
            outputQueue.add(operatorStack.pop())
        }

        return outputQueue
    }

    private fun processRightParenthesis(
        operatorStack: Stack<Token>,
        outputQueue: Queue<Any>,
    ) {
        while (operatorStack.isNotEmpty() && operatorStack.peek().type !is TokenType.LeftParen) {
            outputQueue.add(operatorStack.pop())
        }
        if (operatorStack.isNotEmpty()) {
            operatorStack.pop() // Remove the '('
        }
    }

    private fun processOperator(
        token: Token,
        operatorStack: Stack<Token>,
        outputQueue: Queue<Any>,
    ) {
        while (shouldPopOperator(token, operatorStack)) {
            outputQueue.add(operatorStack.pop())
        }
        operatorStack.push(token)
    }

    private fun shouldPopOperator(
        currentToken: Token,
        operatorStack: Stack<Token>,
    ): Boolean =
        operatorStack.isNotEmpty() &&
            operatorStack.peek().type !is TokenType.LeftParen &&
            precedenceManager.hasHigherOrEqualPrecedence(
                operatorStack.peek().type,
                currentToken.type,
            )
}
