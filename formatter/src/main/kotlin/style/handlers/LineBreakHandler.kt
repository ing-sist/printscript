package style.handlers

import events.Assignment
import events.Colon
import events.Comma
import events.FormattingEvent
import events.Keyword
import events.LeftBrace
import events.LeftParen
import events.Newline
import events.Operator
import events.RightBrace
import events.RightParen
import events.Semicolon
import style.policies.LineBreakPolicy
import style.policies.LineBreakType

object LineBreakHandler : Handler<LineBreakPolicy> {
    override fun handle(
        events: List<FormattingEvent>,
        policy: LineBreakPolicy,
    ): List<FormattingEvent> {
        if (events.isEmpty()) return events

        val out = ArrayList<FormattingEvent>(events.size * 2)
        for (i in events.indices) {
            val cur = events[i]

            emitBefore(cur, policy, out)

            out += cur

            val next = events.getOrNull(i + 1)
            emitAfter(cur, next, policy, out)
        }
        return out
    }
}

private fun emitBefore(
    ev: FormattingEvent,
    p: LineBreakPolicy,
    out: MutableList<FormattingEvent>,
) {
    when (decisionBefore(ev, p)) {
        LineBreakType.ALWAYS -> if (out.lastOrNull() !is Newline) out += Newline
        LineBreakType.NEVER,
        LineBreakType.IF_TOO_LONG,
        -> Unit // el wrap dinámico va en LineWrapHandler
    }
}

private fun emitAfter(
    ev: FormattingEvent,
    next: FormattingEvent?,
    p: LineBreakPolicy,
    out: MutableList<FormattingEvent>,
) {
    when (decisionAfter(ev, p)) {
        LineBreakType.ALWAYS -> if (next !is Newline) out += Newline
        LineBreakType.NEVER,
        LineBreakType.IF_TOO_LONG,
        -> Unit
    }
}

private fun decisionBefore(
    ev: FormattingEvent,
    p: LineBreakPolicy,
): LineBreakType =
    when (ev) {
        is Operator -> p.beforeBinaryOp
        is Assignment -> p.beforeAssignment
        is Comma -> p.beforeComma
        is Semicolon -> p.beforeSemicolon
        is LeftParen, is RightParen -> p.beforeParen
        is LeftBrace, is RightBrace -> p.beforeBracket
        is Keyword -> p.beforeKeyword
        is Colon -> p.beforeColon
        else -> LineBreakType.NEVER
    }

private fun decisionAfter(
    ev: FormattingEvent,
    p: LineBreakPolicy,
): LineBreakType =
    when (ev) {
        is Operator -> p.afterBinaryOp
        is Assignment -> p.afterAssignment
        is Comma -> p.afterComma
        is Semicolon -> p.afterSemicolon
        is LeftParen, is RightParen -> p.afterParen
        is LeftBrace, is RightBrace -> p.afterBracket
        is Keyword -> p.afterKeyword
        is Colon -> p.afterColon
        else -> LineBreakType.NEVER
    }
