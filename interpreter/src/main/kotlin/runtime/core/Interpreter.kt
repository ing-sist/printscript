package runtime.core

import AstNode
import Result
import language.errors.InterpreterException

interface Interpreter {
    fun execute(statement: AstNode): Result<Unit, InterpreterException>
}
