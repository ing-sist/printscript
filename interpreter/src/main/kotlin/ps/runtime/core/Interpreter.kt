package ps.runtime.core

import AstNode
import Result
import ps.lang.errors.InterpreterException

interface Interpreter {
    fun execute(statement: AstNode): Result<Unit, InterpreterException>
}
