package style.policies

sealed class IndentationPolicy {
    abstract fun getIndent(): String

    data class Spaces(
        val size: Int,
    ) : IndentationPolicy() {
        override fun getIndent(): String = " ".repeat(size)
    }

    data object Tabs : IndentationPolicy() {
        override fun getIndent(): String = "\t"
    }
}
