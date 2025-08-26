package events
import style.StyleConfig

data object OpenBrace : FormattingEvent {
    override fun format(
        context: FormatContext,
        out: DocBuilder,
        style: StyleConfig,
        next: FormattingEvent?,
    ) {
        if (!context.atLineStart) out.space()

        out.text("{")
        out.newline()

        context.indent++
        context.atLineStart = true
    }
}

data object CloseBrace : FormattingEvent {
    override fun format(
        context: FormatContext,
        out: DocBuilder,
        style: StyleConfig,
        next: FormattingEvent?,
    ) {
        context.indent-- // este guarda el estado actual

        if (!context.atLineStart) out.newline()

        out.indent(context.indent) // este es el indentado en el que escribo
        out.text("}")

        context.atLineStart = false
    }
}
