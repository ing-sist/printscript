class DocBuilder private constructor(
    private val out: Appendable,
    private val lineStart: Boolean,
    private val lastWasSpace: Boolean,
    private val lastWasNewline: Boolean,
) {
    companion object {
        fun inMemory(): DocBuilder =
            DocBuilder(
                StringBuilder(),
                lineStart = true,
                lastWasSpace = false,
                lastWasNewline = false,
            )

        fun to(target: Appendable): DocBuilder =
            DocBuilder(
                target,
                lineStart = true,
                lastWasSpace = false,
                lastWasNewline = false,
            )
    }

    fun lastWasSpace(): Boolean = lastWasSpace

    fun lastWasNewline(): Boolean = lastWasNewline

    fun isAtLineStart(): Boolean = lineStart

    fun write(s: String): DocBuilder =
        DocBuilder(
            out.append(s),
            lineStart = false,
            lastWasSpace = false,
            lastWasNewline = false,
        )

    fun space(): DocBuilder = DocBuilder(out.append(' '), false, lastWasSpace = true, lastWasNewline = false)

    fun newline(): DocBuilder = DocBuilder(out.append('\n'), lineStart = true, lastWasSpace = false, true)

    fun indent(spaces: Int): DocBuilder {
        if (!lineStart || spaces <= 0) return this
        repeat(spaces) { out.append(' ') }
        return DocBuilder(out, lineStart = false, lastWasSpace = true, false)
    }

    fun build(): String {
        val sb =
            out as? StringBuilder
                ?: error("old.DocBuilder not in memory")
        return sb.toString()
    }
}
