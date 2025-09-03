package events

class DocBuilder {
    private val sb = StringBuilder() // buffer donde se va armando el string final

    fun text(s: String) {
        sb.append(s)
    }

    fun newline() {
        sb.append("\n")
    }

    fun build(): String = sb.toString() // devuelve el string final
}
