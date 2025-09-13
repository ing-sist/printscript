package naming

enum class IdentifierCase {
    CAMEL_CASE {
        override fun isValid(s: String): Boolean =
            s.isNotEmpty() &&
                s[0].isLowerCase() &&
                !s.contains('_') &&
                s.all {
                    it.isLetterOrDigit()
                }

        override fun description(): String = """Identifiers are expected in CamelCase"""
    },
    SNAKE_CASE {
        override fun isValid(s: String): Boolean =
            s.isNotEmpty() &&
                s[0].isLowerCase() &&
                s.all { it.isLowerCase() || it.isDigit() || it == '_' } &&
                !s.contains("__") &&
                s.first() != '_' &&
                s.last() != '_'

        override fun description(): String = """Identifiers are expected in snake_case"""
    }, ;

    abstract fun isValid(s: String): Boolean

    abstract fun description(): String
}
