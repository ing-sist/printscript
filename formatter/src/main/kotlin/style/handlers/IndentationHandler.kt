import events.BlankLine
import events.Dedent
import events.FormattingEvent
import events.Indent
import events.LeftBrace
import events.RightBrace
import style.handlers.Handler
import style.policies.IndentationPolicy
import java.util.ArrayList

object IndentationHandler : Handler<IndentationPolicy> {
    override fun handle(
        events: List<FormattingEvent>,
        policy: IndentationPolicy,
    ): List<FormattingEvent> {
        val out = ArrayList<FormattingEvent>(events.size)

        for (ev in events) {
            when (ev) {
                is LeftBrace -> {
                    out += ev
                    out += BlankLine
                    out += Indent
                }
                is RightBrace -> {
                    out += BlankLine
                    out += Dedent
                    out += ev
                }
                else -> out += ev
            }
        }
        return out
    }
}
