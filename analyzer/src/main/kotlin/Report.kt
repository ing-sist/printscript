class Report(
    private val content: List<Diagnostic>,
) {
    constructor() : this(emptyList())

    fun addDiagnostic(
        ruleId: String,
        message: String,
        location: Location,
        type: Type,
    ): Report {
        val list = content.toMutableList()
        list += Diagnostic(ruleId, message, location, type)
        return Report(list)
    }

    fun isEmpty(): Boolean = content.size == 0

    fun size(): Int {
        if (!isEmpty()) return content.size
        return 0
    }

    fun first(): Diagnostic = content[0]
}
