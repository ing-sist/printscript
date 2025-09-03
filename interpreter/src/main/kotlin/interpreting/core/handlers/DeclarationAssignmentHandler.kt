package interpreting.core.handlers

import DeclarationAssignmentNode
import Result
import interpreting.core.Interpreter
import interpreting.core.StatementHandler
import runtime.InterpreterError

class DeclarationAssignmentHandler : StatementHandler<DeclarationAssignmentNode> {
    override fun handle(
        node: DeclarationAssignmentNode,
        interpreter: Interpreter,
    ): Result<Unit, InterpreterError> {
        val valueResult = interpreter.evaluateExpression(node.value)
        if (valueResult.isFailure) return Result.Failure(valueResult.errorOrNull()!!)

        val value = valueResult.getOrNull()!!
        interpreter.getCurrentEnvironment().define(node.identifier.name, value)
        return Result.Success(Unit)
    }
}
