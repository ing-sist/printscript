package style.handlers

import events.Comment
import events.FormattingEvent
import style.policies.CommentPolicy
import style.policies.CommentType

object CommentHandler : Handler<CommentPolicy> {
    override fun handle(
        events: List<FormattingEvent>,
        policy: CommentPolicy,
    ): List<FormattingEvent> {
        if (policy.type == CommentType.EOL) return events

        val out = ArrayList<FormattingEvent>(events.size)
        for (ev in events) {
            when (ev) {
                is Comment -> {
                    if (policy.type == CommentType.OWN_LINE) {
                        out += ev
                    }
                }
                else -> out += ev
            }
        }
        return out
    }
}
