import rules.implementations.ColonSpacing
import rules.implementations.CommaSpacing
import rules.implementations.IfBraceBelowLine
import rules.implementations.KeywordSpacing
import rules.implementations.LineBreakAfterSemicolon
import rules.implementations.LineBreakBeforePrintln
import rules.implementations.SpaceAroundAssignment
import rules.implementations.SpaceAroundOperators

object FormatterRuleImplementations {
    val IMPLEMENTATIONS =
        listOf(
            IfBraceBelowLine,
            InlineBraceIfStatement,
            LineBreakBeforePrintln,
            LineBreakAfterSemicolon,
            CommaSpacing,
            ColonSpacing,
            SpaceAroundAssignment,
            SpaceAroundOperators,
            KeywordSpacing,
            Indentation,
        )
}
