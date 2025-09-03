package events

// aca solo imprimo el simblo para SRP
// depsues manejo los espacios, breaklines y lo de style config

data object Semicolon : FormattingEvent {
    override fun printer(out: DocBuilder) {
        out.text(";")
    }
}

data class Operator(
    val text: String,
) : FormattingEvent {
    override fun printer(out: DocBuilder) {
        out.text(text)
    }
}

data class Identifier(
    val text: String,
) : FormattingEvent {
    override fun printer(out: DocBuilder) {
        out.text(text)
    }
}

data class Literal(
    val text: String,
) : FormattingEvent {
    override fun printer(out: DocBuilder) {
        out.text(text)
    }
}

data class Keyword(
    val text: String,
) : FormattingEvent { // let, if, when, while...
    override fun printer(out: DocBuilder) {
        out.text(text)
    }
}

data class Assignment(
    val text: String = "=",
) : FormattingEvent {
    override fun printer(out: DocBuilder) {
        out.text(text)
    }
}

data class Comment(
    val text: String,
) : FormattingEvent {
    override fun printer(out: DocBuilder) {
        out.text(text)
    }
}
