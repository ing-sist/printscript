package interpreting.modules

import AssignmentNode
import DeclarationAssignmentNode
import DeclarationNode
import PrintlnNode
import interpreting.core.Interpreter
import interpreting.core.handlers.AssignmentHandler
import interpreting.core.handlers.DeclarationAssignmentHandler
import interpreting.core.handlers.DeclarationHandler
import interpreting.core.handlers.PrintlnHandler

class StatementHandlersModule : InterpreterModule {
    override fun register(interpreter: Interpreter) {
        interpreter.registerStatementHandler(AssignmentNode::class.java, AssignmentHandler())
        interpreter.registerStatementHandler(DeclarationNode::class.java, DeclarationHandler())
        interpreter.registerStatementHandler(DeclarationAssignmentNode::class.java, DeclarationAssignmentHandler())
        interpreter.registerStatementHandler(PrintlnNode::class.java, PrintlnHandler())
    }
}
