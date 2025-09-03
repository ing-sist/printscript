package interpreting.core.handlers

import DeclarationNode
import Result
import interpreting.core.Interpreter
import interpreting.core.StatementHandler
import runtime.InterpreterError
import runtime.Nil

class DeclarationHandler : StatementHandler<DeclarationNode> {
    override fun handle(
        node: DeclarationNode,
        interpreter: Interpreter,
    ): Result<Unit, InterpreterError> {
        interpreter.getCurrentEnvironment().define(node.identifier.name, Nil)
        return Result.Success(Unit)
    }
}
