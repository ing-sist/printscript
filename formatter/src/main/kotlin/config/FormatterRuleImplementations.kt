package config

import Indentation
import rules.implementations.ColonSpacing
import rules.implementations.CommaSpacing
import rules.implementations.IfBraceBelowLine
import rules.implementations.LineBreakAfterSemicolon
import rules.implementations.LineBreakBeforePrintln
import rules.implementations.SpaceAroundAssignment
import rules.implementations.SpaceAroundOperators
import rules.implementations.VarDeclaration

object FormatterRuleImplementations {
    val IMPLEMENTATIONS =
        listOf(
            IfBraceBelowLine,
            LineBreakBeforePrintln,
            LineBreakAfterSemicolon,
            CommaSpacing,
            ColonSpacing,
            SpaceAroundAssignment,
            SpaceAroundOperators,
            VarDeclaration,
            Indentation,
        )
}
