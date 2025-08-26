package events

import style.StyleConfig

data object OpenParen : FormattingEvent {
    override fun format(
        context: FormatContext,
        out: DocBuilder,
        style: StyleConfig,
        next: FormattingEvent?,
    ) {
        out.text("(")
    }
}

data object CloseParen : FormattingEvent {
    override fun format(
        context: FormatContext,
        out: DocBuilder,
        style: StyleConfig,
        next: FormattingEvent?,
    ) {
        out.text(")")
    }
}
