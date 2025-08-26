package events

import style.policies.IndentationPolicy

class DocBuilder(
    private val policy: IndentationPolicy,
) { // cambiar esto para que agarre el indent de la config
    private val sb = StringBuilder() // buffer donde se va armando el string final

    fun text(s: String) {
        sb.append(s)
    }

    fun space() {
        sb.append(" ")
    }

    fun newline() {
        sb.append("\n")
    }

    fun indent(level: Int) {
        repeat(level) { sb.append(policy.getIndent()) }
    }

    fun build(): String = sb.toString() // devuelve el string final
}
