package events

data object LeftBrace : FormattingEvent {
    override fun printer(out: DocBuilder) {
        out.text("{")
    }
}

data object RightBrace : FormattingEvent {
    override fun printer(out: DocBuilder) {
        out.text("}")
    }
}

data object LeftParen : FormattingEvent {
    override fun printer(out: DocBuilder) {
        out.text("(")
    }
}

data object RightParen : FormattingEvent {
    override fun printer(out: DocBuilder) {
        out.text(")")
    }
}

data object Colon : FormattingEvent {
    override fun printer(out: DocBuilder) {
        out.text(":")
    }
}

data object Comma : FormattingEvent {
    override fun printer(out: DocBuilder) {
        out.text(",")
    }
}

data object BlankLine : FormattingEvent {
    override fun printer(out: DocBuilder) {
        out.newline()
        out.newline()
    }
}

data object Indent : FormattingEvent {
    override fun printer(out: DocBuilder) {
        out.text("    ") // ver lo de congid
    }
}

data object Dedent : FormattingEvent {
    override fun printer(out: DocBuilder) {
        // No direct representation in output; handled by formatting logic
    }
}

data object ContinuationIndent : FormattingEvent {
    override fun printer(out: DocBuilder) {
        out.text("    ")
    }
}

data object Space : FormattingEvent {
    override fun printer(out: DocBuilder) {
        out.text(" ")
    }
}

data object Newline : FormattingEvent {
    override fun printer(out: DocBuilder) {
        out.newline()
    }
}

data object Eof
