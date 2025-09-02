package style.handlers

import events.BlankLine
import events.Comma
import events.Dedent
import events.FormattingEvent
import events.Indent
import events.LeftParen
import events.RightParen
import style.policies.ArgsLayoutPolicy

object ArgsLayoutHandler : Handler<ArgsLayoutPolicy> {
    override fun handle(
        events: List<FormattingEvent>,
        policy: ArgsLayoutPolicy,
    ): List<FormattingEvent> {
        if (policy == ArgsLayoutPolicy.INLINE) return events

        val out = ArrayList<FormattingEvent>(events.size)
        for (ev in events) {
            when (ev) {
                is LeftParen -> {
                    out += ev
                    out += BlankLine
                    out += Indent // aumentar indent para los args
                }
                is Comma -> {
                    out += ev
                    out += BlankLine
                }
                is RightParen -> {
                    out += BlankLine
                    out += Dedent // restaurar indent
                    out += ev
                }
                else -> out += ev
            }
        }
        return out
    }
}
