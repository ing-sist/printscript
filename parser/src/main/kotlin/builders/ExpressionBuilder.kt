package builders

import AstNode
import BinaryOperationNode
import Token
import TokenType
import UnaryOperationNode
import builders.expresionHelpers.AstNodeFactory
import builders.expresionHelpers.OperatorPrecedenceManager
import builders.expresionHelpers.ShuntingYardConverter
import java.util.Queue
import java.util.Stack

class ExpressionBuilder : AstBuilder {
    private val operatorPrecedenceManager = OperatorPrecedenceManager()
    private val shuntingYardConverter = ShuntingYardConverter(operatorPrecedenceManager)
    private val astNodeFactory = AstNodeFactory()

    override fun build(tokens: List<Token>): AstNode {
        val rpnQueue = shuntingYardConverter.convertToRPN(tokens)
        return buildASTFromRPN(rpnQueue)
    }

    private fun buildASTFromRPN(rpnQueue: Queue<Any>): AstNode {
        val stack: Stack<AstNode> = Stack()

        while (rpnQueue.isNotEmpty()) {
            when (val element = rpnQueue.poll()) {
                is AstNode -> stack.push(element)
                is Token -> {
                    if (isOperator(element)) {
                        // Check if this is a unary operator (only one operand available)
                        if (stack.size == 1 && isUnaryOperator(element)) {
                            val operand = stack.pop()
                            stack.push(UnaryOperationNode(element, operand))
                        } else if (stack.size >= 2) {
                            // Binary operator
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

    private fun isUnaryOperator(token: Token): Boolean = token.type is TokenType.Minus || token.type is TokenType.Plus
}
