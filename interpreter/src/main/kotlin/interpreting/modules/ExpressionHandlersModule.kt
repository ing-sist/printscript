package interpreting.modules

import BinaryOperationNode
import IdentifierNode
import LiteralNode
import UnaryOperationNode
import interpreting.core.Interpreter
import interpreting.core.handlers.BinaryOperationHandler
import interpreting.core.handlers.IdentifierHandler
import interpreting.core.handlers.LiteralHandler
import interpreting.core.handlers.UnaryOperationHandler

class ExpressionHandlersModule : InterpreterModule {
    override fun register(interpreter: Interpreter) {
        interpreter.registerExpressionHandler(LiteralNode::class.java, LiteralHandler())
        interpreter.registerExpressionHandler(IdentifierNode::class.java, IdentifierHandler())
        interpreter.registerExpressionHandler(BinaryOperationNode::class.java, BinaryOperationHandler())
        interpreter.registerExpressionHandler(UnaryOperationNode::class.java, UnaryOperationHandler())
    }
}
