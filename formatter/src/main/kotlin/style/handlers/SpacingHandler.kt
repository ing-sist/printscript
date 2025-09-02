package style.handlers

import events.Assignment
import events.Colon
import events.Comma
import events.FormattingEvent
import events.Keyword
import events.Operator
import events.RightParen
import events.Semicolon
import events.Space
import style.policies.AssignmentSpacing
import style.policies.BinaryOpSpacing
import style.policies.ColonSpacing
import style.policies.CommaSpacing
import style.policies.KeywordSpacing
import style.policies.SpacingPolicy

object SpacingHandler : Handler<SpacingPolicy> {
    override fun handle(
        events: List<FormattingEvent>,
        policy: SpacingPolicy,
    ): List<FormattingEvent> {
        val base = events.filterNot { it is Space }
        val out = ArrayList<FormattingEvent>(base.size * 2)

        for (i in base.indices) {
            val cur = base[i]
            val next = base.getOrNull(i + 1)
            out += cur
            if (next != null && needsSpaceBetween(cur, next, policy)) {
                out += Space
            }
        }
        return out
    }
}

private fun isForbiddenPair(next: FormattingEvent): Boolean = next is Semicolon || next is RightParen

private fun needsSpaceBetween(
    cur: FormattingEvent,
    next: FormattingEvent,
    p: SpacingPolicy,
): Boolean {
    if (isForbiddenPair(next)) return false

    val rules =
        listOf(
            (cur is Comma) && p.comma == CommaSpacing.AFTER,
            (cur is Colon) && p.colon == ColonSpacing.AFTER,
            (cur is Assignment || next is Assignment) && p.assignment == AssignmentSpacing.AROUND,
            (cur is Operator || next is Operator) && p.binaryOp == BinaryOpSpacing.AROUND,
            (cur is Keyword) && p.keyword == KeywordSpacing.AFTER,
        )
    return rules.any { it } // uso any para bajar complejidad
}
