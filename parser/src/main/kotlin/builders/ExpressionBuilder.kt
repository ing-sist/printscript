package builders

import AstNode
import BinaryOperationNode
import Result
import Token
import TokenType
import UnaryOperationNode
import builders.expresionHelpers.AstNodeFactory
import builders.expresionHelpers.OperatorPrecedenceManager
import builders.expresionHelpers.ShuntingYardConverter
import parser.ParseError
import java.util.Queue
import java.util.Stack

class ExpressionBuilder : AstBuilder {
    private val operatorPrecedenceManager = OperatorPrecedenceManager()
    private val shuntingYardConverter = ShuntingYardConverter(operatorPrecedenceManager)
    private val astNodeFactory = AstNodeFactory()

    // Existing API preserved: throws on failure
    override fun build(tokens: List<Token>): AstNode =
        when (val r = buildResult(tokens)) {
            is Result.Success -> r.value
            is Result.Failure -> throw IllegalArgumentException(r.error.toString())
        }

    // New non-throwing API
    fun buildResult(tokens: List<Token>): Result<AstNode, ParseError> =
        when (val rpn = shuntingYardConverter.convertToRPNResult(tokens)) {
            is Result.Success -> Result.Success(buildASTFromRPN(rpn.value))
            is Result.Failure -> Result.Failure(rpn.error)
        }

    private fun buildASTFromRPN(rpnQueue: Queue<Any>): AstNode {
        val stack: Stack<AstNode> = Stack()

        while (rpnQueue.isNotEmpty()) {
            when (val element = rpnQueue.poll()) {
                is AstNode -> stack.push(element)
                is Token -> {
                    if (isOperator(element)) {
                        if (stack.size == 1 && isUnaryOperator(element)) {
                            val operand = stack.pop()
                            stack.push(UnaryOperationNode(element, operand))
                        } else if (stack.size >= 2) {
                            val right = stack.pop()
                            val left = stack.pop()
                            stack.push(BinaryOperationNode(left, element, right))
                        }
                    } else {
                        stack.push(astNodeFactory.createFromToken(element))
                    }
                }
            }
        }

        return stack.pop()
    }

    private fun isOperator(token: Token): Boolean = operatorPrecedenceManager.isOperator(token.type)

    private fun isUnaryOperator(token: Token): Boolean =
        token.type is TokenType.Operator.Minus ||
            token.type is TokenType.Operator.Plus
}
