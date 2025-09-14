import utils.Type

data class Diagnostic(
    val ruleId: String,
    val message: String,
    val location: Location,
    val type: Type,
)

interface DiagnosticSender {
    fun emit(d: Diagnostic)
}
