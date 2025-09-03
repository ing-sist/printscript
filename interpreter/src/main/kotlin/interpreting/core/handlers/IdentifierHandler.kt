package interpreting.core.handlers

import IdentifierNode
import Result
import interpreting.core.ExpressionHandler
import interpreting.core.Interpreter
import runtime.InterpreterError
import runtime.Value

class IdentifierHandler : ExpressionHandler<IdentifierNode> {
    override fun handle(
        node: IdentifierNode,
        interpreter: Interpreter,
    ): Result<Value, InterpreterError> {
        val result = interpreter.getCurrentEnvironment().get(node.name)
        return if (result.isFailure) {
            // Re-create the error with the proper location from the token
            Result.Failure(InterpreterError("Undefined variable '${node.name}'", node.value.location))
        } else {
            result
        }
    }
}
