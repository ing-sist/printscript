enum class IdentifierCase { CAMEL, SNAKE }

object Naming {
    fun isCamelCase(s: String): Boolean =
        s.isNotEmpty() &&
            s[0].isLowerCase() &&
            !s.contains('_') &&
            s.all { it.isLetterOrDigit() }

    fun isSnakeCase(s: String): Boolean =
        s.isNotEmpty() &&
            s[0].isLowerCase() &&
            s.all { it.isLowerCase() || it.isDigit() || it == '_' } &&
            !s.contains("__") &&
            s.first() != '_' &&
            s.last() != '_'
}
