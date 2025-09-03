package interpreting.core.handlers

import PrintlnNode
import Result
import interpreting.core.Interpreter
import interpreting.core.StatementHandler
import runtime.InterpreterError

class PrintlnHandler : StatementHandler<PrintlnNode> {
    override fun handle(
        node: PrintlnNode,
        interpreter: Interpreter,
    ): Result<Unit, InterpreterError> {
        val valueResult = interpreter.evaluateExpression(node.content)
        if (valueResult.isFailure) return Result.Failure(valueResult.errorOrNull()!!)

        val value = valueResult.getOrNull()!!
        interpreter.getCurrentOutput().println(value.stringify())
        return Result.Success(Unit)
    }
}
