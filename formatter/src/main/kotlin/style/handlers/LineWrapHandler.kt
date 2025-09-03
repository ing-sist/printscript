package style.handlers

import events.Assignment
import events.Colon
import events.Comma
import events.ContinuationIndent
import events.FormattingEvent
import events.Newline
import events.Operator
import events.RightParen
import events.Semicolon
import events.Space
import style.policies.LineWrapMode
import style.policies.LineWrapPolicy

object LineWrapHandler : Handler<LineWrapPolicy> {
    override fun handle(
        events: List<FormattingEvent>,
        policy: LineWrapPolicy,
    ): List<FormattingEvent> =
        when (policy.mode) {
            LineWrapMode.NEVER -> events
            LineWrapMode.ALWAYS -> wrapAlways(events, policy)
            LineWrapMode.IF_TOO_LONG -> wrapIfTooLong(events, policy)
        }
}

private fun evWidth(
    ev: FormattingEvent,
    continuationIndent: Int,
): Int =
    when (ev) {
        is Newline -> 0
        is ContinuationIndent -> continuationIndent
        else -> ev.toString().length
    }

private fun isSoftBreakBetween(
    cur: FormattingEvent,
    next: FormattingEvent?,
): Boolean {
    val softByCur = cur is Comma || cur is Operator || cur is Assignment || cur is Colon || cur is Space
    val hardNo = (next is RightParen) || (next is Semicolon)
    return softByCur && !hardNo
}

private fun insertWrapAt(
    out: MutableList<FormattingEvent>,
    indexAfter: Int,
) {
    out.add(indexAfter, Newline)
    out.add(indexAfter + 1, ContinuationIndent)
}

private fun recalcCol(
    out: List<FormattingEvent>,
    startIdx: Int,
    base: Int,
    continuationIndent: Int,
): Int {
    var col = base
    var i = startIdx
    while (i < out.size) {
        val e = out[i]
        col = if (e is Newline) 0 else col + evWidth(e, continuationIndent)
        i++
    }
    return col
}

private fun wrapAlways(
    events: List<FormattingEvent>,
    policy: LineWrapPolicy,
): List<FormattingEvent> {
    if (events.isEmpty()) return events

    val out = ArrayList<FormattingEvent>(events.size * 2)
    var lastSoftBreakIdx = -1
    var col = 0

    for (i in events.indices) {
        val cur = events[i]
        val next = events.getOrNull(i + 1)

        out += cur
        if (cur is Newline) {
            col = 0
            lastSoftBreakIdx = -1
            continue
        }

        col += evWidth(cur, policy.continuationIndent)
        if (isSoftBreakBetween(cur, next)) {
            lastSoftBreakIdx = out.lastIndex
            // cortamos inmediatamente en cada soft break
            insertWrapAt(out, out.lastIndex)
            col = policy.continuationIndent
            lastSoftBreakIdx = -1
        }
    }
    return out
}

private fun wrapIfTooLong(
    events: List<FormattingEvent>,
    policy: LineWrapPolicy,
): List<FormattingEvent> {
    if (events.isEmpty()) return events

    val out = ArrayList<FormattingEvent>(events.size * 2)
    var col = 0
    var lastSoftBreakIdx = -1

    fun emit(
        cur: FormattingEvent,
        next: FormattingEvent?,
    ) {
        out += cur
        if (cur is Newline) {
            col = 0
            lastSoftBreakIdx = -1
            return
        }
        col += evWidth(cur, policy.continuationIndent)
        if (isSoftBreakBetween(cur, next)) lastSoftBreakIdx = out.lastIndex
    }

    fun wrapAtSoftBreakOrBeforeCurrent(next: FormattingEvent?) {
        if (col <= policy.maxLineLength) return
        if (lastSoftBreakIdx != -1) {
            val insertAt = lastSoftBreakIdx + 1
            insertWrapAt(out, insertAt)
            col = recalcCol(out, insertAt + 2, policy.continuationIndent, policy.continuationIndent)
            lastSoftBreakIdx = -1
            return
        }
        // fallback: cortar antes del último emitido
        val last = out.removeLast()
        insertWrapAt(out, out.size)
        col = policy.continuationIndent
        emit(last, next)
    }

    for (i in events.indices) {
        val cur = events[i]
        val next = events.getOrNull(i + 1)
        emit(cur, next)
        wrapAtSoftBreakOrBeforeCurrent(next)
    }
    return out
}
