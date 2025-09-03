package interpreting.core

import AstNode
import Result
import runtime.InterpreterError

/**
 * Registro de handlers que mapea tipos de nodos AST a sus handlers correspondientes
 */
class HandlerRegistry<R> {
    /**
     * Guardo en el value un funcion con dos parametros que devuelte R
     * Luego la voy a usar para ejecutar la funcion que necesite del nodo especifico de mis distintos handlers
     */
    private val handlers = mutableMapOf<Class<out AstNode>, (AstNode, Interpreter) -> R>()

    /**
     * Registra un handler para un tipo específico de nodo
     */
    fun <N : AstNode> register(
        nodeClass: Class<N>,
        handler: (N, Interpreter) -> R,
    ) {
        @Suppress("UNCHECKED_CAST")
        handlers[nodeClass] = handler as (AstNode, Interpreter) -> R
    }

    /**
     * Conecta un nodo al handler correspondiente
     */
    @Suppress("UNCHECKED_CAST")
    fun connect(
        node: AstNode,
        interpreter: Interpreter,
    ): R {
        val handler = handlers[node::class.java]
        return if (handler != null) {
            handler(node, interpreter)
        } else {
            Result.Failure(
                InterpreterError(
                    "No handler registered for ${node::class.java.simpleName}",
                    null,
                ),
            ) as R
        }
    }
}
