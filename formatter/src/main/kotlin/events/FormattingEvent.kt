package events

sealed interface FormattingEvent {
    fun printer(out: DocBuilder)
}
