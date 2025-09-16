package config

import rules.definitions.IndentationDef
import rules.definitions.InlineIfBraceIfStatementDef
import rules.definitions.LineBreakAfterSemiColonDef
import rules.definitions.LineBreakBeforePrintlnDef
import rules.definitions.SpaceAfterColonDef
import rules.definitions.SpaceAroundAssignmentDef
import rules.definitions.SpaceAroundOperatorsDef
import rules.definitions.SpaceBeforeColonDef

data class Alias(
    val targetId: String,
    val transform: (String) -> String = { it },
)

private fun asBool(v: String) = v.trim().lowercase() == "true"

object AliasesMap {
    val ALIASES: Map<String, Alias> =
        mapOf(
            "enforce-spacing-before-colon-in-declaration" to
                Alias(SpaceBeforeColonDef.id) { v ->
                    (asBool(v)).toString()
                },
            "enforce-spacing-after-colon-in-declaration" to
                Alias(SpaceAfterColonDef.id) { v ->
                    (asBool(v)).toString()
                },
            "enforce-spacing-around-equals" to Alias(SpaceAroundAssignmentDef.id),
            "enforce-no-spacing-around-equals" to
                Alias(SpaceAroundAssignmentDef.id) { v ->
                    (!asBool(v)).toString()
                },
            "mandatory-space-surrounding-operations" to Alias(SpaceAroundOperatorsDef.id),
            "mandatory-line-break-after-statement" to Alias(LineBreakAfterSemiColonDef.id),
            "line-breaks-after-println" to Alias(LineBreakBeforePrintlnDef.id),
            "indentation-size" to Alias(IndentationDef.id),
            "indent-inside-if" to Alias(IndentationDef.id),
            "if-brace-same-line" to Alias(InlineIfBraceIfStatementDef.id),
            "if-brace-below-line" to
                Alias(InlineIfBraceIfStatementDef.id) { v ->
                    (!asBool(v)).toString()
                },
            "mandatory-single-space-separation" to Alias(SpaceAroundAssignmentDef.id),
        )

//    val ALIASES =
//        mapOf(
//            "enforce-spacing-around-equals" to "spaceAroundAssignment",
//            "enforce-no-spacing-around-equals" to "noSpaceAroundAssignment",
//            "enforce-spacing-after-colon-in-declaration" to "spaceAfterColonInDecl",
//            "enforce-spacing-before-colon-in-declaration" to "spaceBeforeColonInDecl",
//            "indent_size" to "indentSpaces",
//            "indent-spaces" to "indentSpaces",
//            "indent-inside-if" to "indentSpaces",
//            "tabsize" to "indentSpaces",
//            "line-breaks-after-println" to "blankLinesAfterPrintln",
//            "line_breaks_after_println" to "blankLinesAfterPrintln",
//            "mandatory-single-space-separation" to "mandatorySingleSpaceSeparation",
//            "if-brace-below-line" to "ifBraceBelowLine",
//            "if-brace-same-line" to "ifBraceSameLine",
//        )
}
