package events
import style.StyleConfig

sealed interface FormattingEvent {
    fun format(
        context: FormatContext,
        out: DocBuilder,
        style: StyleConfig,
        next: FormattingEvent?,
    )
}
