class DocBuilder private constructor(
    private val content: String,
) {
    constructor() : this("")

    fun write(s: String): DocBuilder = DocBuilder(content + s)

    fun newline(): DocBuilder = DocBuilder(content + "\n")

    fun space(): DocBuilder = DocBuilder(content + " ")

    fun build(): String = content
}
