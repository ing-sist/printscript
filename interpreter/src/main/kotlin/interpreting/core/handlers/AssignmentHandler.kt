package interpreting.core.handlers

import AssignmentNode
import Result
import interpreting.core.Interpreter
import interpreting.core.StatementHandler
import runtime.InterpreterError

class AssignmentHandler : StatementHandler<AssignmentNode> {
    override fun handle(
        node: AssignmentNode,
        interpreter: Interpreter,
    ): Result<Unit, InterpreterError> {
        val valueResult = interpreter.evaluateExpression(node.expression)
        if (valueResult.isFailure) return Result.Failure(valueResult.errorOrNull()!!)

        val value = valueResult.getOrNull()!!

        return interpreter
            .getCurrentEnvironment()
            .assign(node.identifier.name, value)
            .map { }
    }
}
