package interpreting.core

import AstNode
import Result
import runtime.InterpreterError
import runtime.Value

fun interface ExpressionHandler<N : AstNode> {
    fun handle(
        node: N,
        interpreter: Interpreter,
    ): Result<Value, InterpreterError>
}

fun interface StatementHandler<N : AstNode> {
    fun handle(
        node: N,
        interpreter: Interpreter,
    ): Result<Unit, InterpreterError>
}
