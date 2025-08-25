package dsl

import Result
import parser.ParseError

/**
 * Resultado del parsing con métodos de verificación fluidos.
 */
class ParseResult(
    private val result: Result<List<Any>, ParseError>,
    private val expectingSuccess: Boolean,
) {
    fun withStatementCount(count: Int): ParseResult {
        if (expectingSuccess) {
            require(result.isSuccess) {
                "Se esperaba parsing exitoso pero falló: ${result.errorOrNull()}"
            }
            val statements = result.getOrNull()!!
            require(statements.size == count) {
                "Se esperaban $count statements pero se obtuvieron ${statements.size}"
            }
        }
        return this
    }

    fun withStatementType(
        index: Int,
        type: Class<*>,
    ): ParseResult {
        if (expectingSuccess) {
            val statements = result.getOrNull()!!
            require(type.isInstance(statements[index])) {
                val actualType = statements[index]::class.simpleName
                "Se esperaba que el statement en índice $index fuera de tipo ${type.simpleName} pero era $actualType"
            }
        }
        return this
    }

    fun withErrorType(errorType: Class<out ParseError>): ParseResult {
        if (!expectingSuccess) {
            require(result.isFailure) { "Se esperaba que el parsing fallara pero tuvo éxito" }
            val error = result.errorOrNull()!!
            require(errorType.isInstance(error)) {
                "Se esperaba error de tipo ${errorType.simpleName} pero se obtuvo ${error::class.simpleName}"
            }
        }
        return this
    }

    fun getStatements() = result.getOrNull()!!

    fun getStatement(index: Int) = getStatements()[index]
}
