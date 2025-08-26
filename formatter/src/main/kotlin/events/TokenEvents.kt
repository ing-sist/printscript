package events
import style.StyleConfig

data object Semicolon : FormattingEvent {
    override fun format(
        context: FormatContext,
        out: DocBuilder,
        style: StyleConfig,
        next: FormattingEvent?,
    ) {
        out.text(";")
        out.newline()

        context.atLineStart = true
    }
}

data object Comma : FormattingEvent {
    override fun format(
        context: FormatContext,
        out: DocBuilder,
        style: StyleConfig,
        next: FormattingEvent?,
    ) {
        out.text(",")
        out.space()

        context.atLineStart = false
    }
}

data class Operator(
    val text: String,
) : FormattingEvent {
    override fun format(
        context: FormatContext,
        out: DocBuilder,
        style: StyleConfig,
        next: FormattingEvent?,
    ) {
        out.space()
        out.text("text")
        out.space()
        context.atLineStart = false
    }
}

data class Identifier(
    val text: String,
) : FormattingEvent {
    override fun format(
        context: FormatContext,
        out: DocBuilder,
        style: StyleConfig,
        next: FormattingEvent?,
    ) {
        if (context.atLineStart) {
            out.indent(context.indent)
            context.atLineStart = false
        }

        out.text(text)

        // Si lo que sigue es otra "palabra" (id/keyword/literal), separá con espacio para no pegar “tokens”
        if (isWordLike(next)) out.space()

        context.atLineStart = false
    }
}

data class Literal(
    val text: String,
) : FormattingEvent {
    override fun format(
        context: FormatContext,
        out: DocBuilder,
        style: StyleConfig,
        next: FormattingEvent?,
    ) {
        if (context.atLineStart) {
            out.indent(context.indent)
            context.atLineStart = false
        }
        out.text(text)

        // Igual criterio que Identifier: evitar pegar con otra "palabra" justo después
        if (isWordLike(next)) out.space()

        context.atLineStart = false
    }
}

data class Keyword(
    val text: String,
) : FormattingEvent { // let, if, when, while...
    override fun format(
        context: FormatContext,
        out: DocBuilder,
        style: StyleConfig,
        next: FormattingEvent?,
    ) {
        if (context.atLineStart) {
            out.indent(context.indent)
            context.atLineStart = false
        }
        out.text(text) // primero imprimo el keyword

        when (next) {
            // sin espacio desp
            is Semicolon,
            is Comma,
            is CloseParen,
            is CloseBrace,
            null,
            -> {
            }

            // debo check policies
            is OpenParen -> {
                if (style.parenPolicy.spaceBeforeOpening) {
                    out.space()
                }
            }

            is OpenBrace -> {
                if (style.bracePolicy.spaceBeforeOpening) {
                    out.space()
                }
            }

            else -> {
                out.space()
            }
        }

        context.atLineStart = false
    }
}

data object Eof : FormattingEvent {
    override fun format(
        context: FormatContext,
        out: DocBuilder,
        style: StyleConfig,
        next: FormattingEvent?,
    ) {
        out.newline()
    }
}

private fun isWordLike(next: FormattingEvent?) =
    next is Identifier ||
        next is Literal ||
        next is Keyword
