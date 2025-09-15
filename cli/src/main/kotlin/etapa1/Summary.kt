package etapa1

data class Summary(
    val operation: String,
    val specVersion: String,
    val filesProcessed: Int,
    val errors: Int,
    val warnings: Int,
    val timeMs: Long
)