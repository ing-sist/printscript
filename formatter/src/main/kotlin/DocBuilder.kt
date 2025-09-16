class DocBuilder private constructor(
    private val out: Appendable,
    private val lineStart: Boolean,
    private val written: Int,
) {
    companion object {
        fun inMemory(): DocBuilder = DocBuilder(StringBuilder(), true, 0)

        fun to(target: Appendable): DocBuilder = DocBuilder(target, true, 0)
    }

    fun getWrittenCount(): Int = written

    fun isAtLineStart(): Boolean = lineStart

    fun write(s: String): DocBuilder = DocBuilder(out.append(s), lineStart = false, written = written + 1)

    fun space(): DocBuilder = DocBuilder(out.append(' '), lineStart = false, written = written + 1)

    fun newline(): DocBuilder = DocBuilder(out.append('\n'), lineStart = true, written = written + 1)

    fun indent(spaces: Int): DocBuilder {
        if (!lineStart || spaces <= 0) return this
        repeat(spaces) { out.append(' ') }
        return DocBuilder(out, lineStart = false, written = written + 1)
    }

    fun build(): String {
        val sb =
            out as? StringBuilder
                ?: error("DocBuilder not in memory")
        return sb.toString()
    }
}
