package config

import Indentation
import InlineBraceIfStatement
import rules.implementations.ColonSpacing
import rules.implementations.CommaSpacing
import rules.implementations.KeywordSpacing
import rules.implementations.LineBreakAfterPrintln
import rules.implementations.LineBreakAfterSemicolon
import rules.implementations.SpaceAroundAssignment
import rules.implementations.SpaceAroundOperators
import rules.implementations.VarDeclaration

object FormatterRuleImplementations {
    val IMPLEMENTATIONS =
        listOf(
            InlineBraceIfStatement,
            LineBreakAfterPrintln,
            LineBreakAfterSemicolon,
            KeywordSpacing,
            CommaSpacing,
            ColonSpacing,
            SpaceAroundAssignment,
            SpaceAroundOperators,
            VarDeclaration,
            Indentation,
        )
}
