class DocBuilder private constructor(
    private val out: Appendable,
    private val lineStart: Boolean,
) {
    companion object {
        fun inMemory(): DocBuilder = DocBuilder(StringBuilder(), true)

        fun to(target: Appendable): DocBuilder = DocBuilder(target, true)
    }

    fun getLastSent(): Char? {
        val sb = out as? StringBuilder ?: return null
        return if (sb.isEmpty()) null else sb[sb.length - 1]
    }

    fun isAtLineStart(): Boolean = lineStart

    fun write(s: String): DocBuilder = DocBuilder(out.append(s), lineStart = false)

    fun space(): DocBuilder = DocBuilder(out.append(' '), lineStart = false)

    fun newline(): DocBuilder = DocBuilder(out.append('\n'), lineStart = true)

    fun indent(spaces: Int): DocBuilder {
        if (!lineStart || spaces <= 0) return this
        repeat(spaces) { out.append(' ') }
        return DocBuilder(out, lineStart = false)
    }

    fun build(): String {
        val sb =
            out as? StringBuilder
                ?: error("old.DocBuilder not in memory")
        return sb.toString()
    }
}
