data class Diagnostic(
    val ruleId: String,
    val message: String,
    val location: Location,
    val type: Type,
)
