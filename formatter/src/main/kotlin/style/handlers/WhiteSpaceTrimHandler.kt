package style.handlers

import events.BlankLine
import events.FormattingEvent
import events.Space
import style.policies.WhitespaceTrimPolicy

object WhiteSpaceTrimHandler : Handler<WhitespaceTrimPolicy> {
    override fun handle(
        events: List<FormattingEvent>,
        policy: WhitespaceTrimPolicy,
    ): List<FormattingEvent> {
        if (policy == WhitespaceTrimPolicy.NONE) return events

        val out = ArrayList<FormattingEvent>(events.size)
        var pendingSpace = false // lo dejo pendiente para evitar doble space

        for (ev in events) {
            when (ev) {
                is Space -> {
                    pendingSpace = true
                }

                is BlankLine -> {
                    pendingSpace = false // blank line > space
                    out += ev
                }

                else -> {
                    if (pendingSpace) {
                        out += Space
                        pendingSpace = false
                    }
                    out += ev
                }
            }
        }
        // no printeo el space pendiente al final
        return out
    }
}
