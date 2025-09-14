package config

import rules.definitions.IndentationDef
import rules.definitions.InlineIfBraceIfStatementDef
import rules.definitions.LineBreakAfterSemiColonDef
import rules.definitions.LineBreakBeforePrintlnDef
import rules.definitions.Rule
import rules.definitions.SpaceAfterColonDef
import rules.definitions.SpaceAroundAssignmentDef
import rules.definitions.SpaceAroundOperatorsDef
import rules.definitions.SpaceBeforeColonDef

object RuleDefinitions {
    val RULES: List<Rule<*>> =
        listOf(
            LineBreakBeforePrintlnDef,
            LineBreakAfterSemiColonDef,
            SpaceBeforeColonDef,
            SpaceAfterColonDef,
            SpaceAroundAssignmentDef,
            SpaceAroundOperatorsDef,
            InlineIfBraceIfStatementDef,
            IndentationDef,
        )
}
