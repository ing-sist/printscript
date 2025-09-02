data class Diagnostic(
    val ruleId: String,
    val message: String,
    val location: Location,
    val severity: Type,
)
