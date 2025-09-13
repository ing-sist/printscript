class DocBuilder(
    private val content: String = "",
    private val lineStart: Boolean = true,
) {
    fun write(s: String): DocBuilder = DocBuilder(content + s, lineStart = false)

    fun space(): DocBuilder =
        if (lineStart) {
            this
        } else {
            DocBuilder(content + " ", lineStart = false)
        }

    fun newline(): DocBuilder = DocBuilder(content + "\n", lineStart = true)

    fun isAtLineStart(): Boolean = lineStart

    fun build(): String = content

    fun indent(spaces: Int): DocBuilder {
        var d = this
        if (lineStart) {
            repeat(spaces) { d = d.write(" ") }
        }
        return d
    }
}
