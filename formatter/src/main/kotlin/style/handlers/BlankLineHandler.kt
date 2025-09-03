import events.BlankLine
import events.FormattingEvent
import style.handlers.Handler
import style.policies.BlankLinePolicy

object BlankLineHandler : Handler<BlankLinePolicy> {
    override fun handle(
        events: List<FormattingEvent>,
        policy: BlankLinePolicy,
    ): List<FormattingEvent> {
        if (policy == BlankLinePolicy.PRESERVE_ALL) return events

        val out = ArrayList<FormattingEvent>(events.size)
        var blankStreak = 0

        for (ev in events) {
            if (ev is BlankLine) {
                blankStreak++ //
                when (policy) {
                    BlankLinePolicy.NONE -> {
                        continue
                    }
                    BlankLinePolicy.AT_MOST_ONE -> {
                        if (blankStreak == 1) out.add(ev) // una sola
                    }
                    else -> { } // ya cubri rriba el preserve all
                }
            } else {
                blankStreak = 0
                out.add(ev) // agrego el evento normal
            }
        }
        return out
    }
}
