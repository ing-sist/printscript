package interpreting.core

import AstNode
import Result
import runtime.Environment
import runtime.InterpreterError
import runtime.Output
import runtime.Value

/**
 * Intérprete principal que ejecuta el AST
 */
class Interpreter(
    private val context: InterpreterContext,
    private val expressionRegistry: HandlerRegistry<Result<Value, InterpreterError>>,
    private val statementRegistry: HandlerRegistry<Result<Unit, InterpreterError>>,
) {
    /**
     * Evalúa una expresión
     */
    fun evaluateExpression(expression: AstNode): Result<Value, InterpreterError> =
        expressionRegistry.connect(
            expression,
            this,
        )

    /**
     * Ejecuta una declaración
     */
    fun executeStatement(statement: AstNode): Result<Unit, InterpreterError> =
        statementRegistry.connect(
            statement,
            this,
        )

    /**
     * Ejecuta una lista completa de declaraciones.
     * Si alguna falla, retorna el primer error encontrado.
     */
    fun runProgram(statements: List<AstNode>): Result<Unit, InterpreterError> {
        for (statement in statements) {
            val result = executeStatement(statement)
            if (result.isFailure) {
                return result
            }
        }
        return Result.Success(Unit)
    }

    /**
     * Obtiene el entorno de variables actual.
     */
    fun getCurrentEnvironment(): Environment = context.environment

    /**
     * Obtiene el manejador de salida actual.
     */
    fun getCurrentOutput(): Output = context.output

    /**
     * Registra un manejador para expresiones de un tipo específico.
     */
    fun <N : AstNode> registerExpressionHandler(
        nodeClass: Class<N>,
        handler: (N, Interpreter) -> Result<Value, InterpreterError>,
    ) {
        expressionRegistry.register(nodeClass, handler)
    }

    /**
     * Registra un manejador para expresiones usando la interfaz ExpressionHandler.
     */
    fun <N : AstNode> registerExpressionHandler(
        nodeClass: Class<N>,
        handler: ExpressionHandler<N>,
    ) {
        registerExpressionHandler(nodeClass) { node, interpreter ->
            handler.handle(node, interpreter)
        }
    }

    /**
     * Registra un manejador para declaraciones de un tipo específico.
     */
    fun <N : AstNode> registerStatementHandler(
        nodeClass: Class<N>,
        handler: (N, Interpreter) -> Result<Unit, InterpreterError>,
    ) {
        statementRegistry.register(nodeClass, handler)
    }

    /**
     * Registra un manejador para statements usando la interfaz StatementHandler.
     */
    fun <N : AstNode> registerStatementHandler(
        nodeClass: Class<N>,
        handler: StatementHandler<N>,
    ) {
        registerStatementHandler(nodeClass) { node, interpreter ->
            handler.handle(node, interpreter)
        }
    }
}
