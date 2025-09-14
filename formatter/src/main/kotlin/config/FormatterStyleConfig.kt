package config

import config.RuleDefinitions.RULES
import rules.definitions.IndentationDef
import rules.definitions.InlineIfBraceIfStatementDef
import rules.definitions.LineBreakAfterSemiColonDef
import rules.definitions.LineBreakBeforePrintlnDef
import rules.definitions.SpaceAfterColonDef
import rules.definitions.SpaceAroundAssignmentDef
import rules.definitions.SpaceAroundOperatorsDef
import rules.definitions.SpaceBeforeColonDef
import java.io.File

data class FormatterStyleConfig(
    val lineBreakBeforePrintln: Int,
    val lineBreakAfterSemicolon: Boolean,
    val spaceBeforeColon: Boolean,
    val spaceAfterColon: Boolean,
    val spaceAroundAssignment: Boolean,
    val spaceAroundOperators: Boolean,
    val indentation: Int,
    val inlineIfBraceIfStatement: Boolean,
) {
    companion object {
        fun fromMap(style: Map<String, Any>): FormatterStyleConfig =
            FormatterStyleConfig(
                lineBreakBeforePrintln = style[LineBreakBeforePrintlnDef.id] as Int,
                lineBreakAfterSemicolon = style[LineBreakAfterSemiColonDef.id] as Boolean,
                spaceBeforeColon = style[SpaceBeforeColonDef.id] as Boolean,
                spaceAfterColon = style[SpaceAfterColonDef.id] as Boolean,
                spaceAroundAssignment = style[SpaceAroundAssignmentDef.id] as Boolean,
                spaceAroundOperators = style[SpaceAroundOperatorsDef.id] as Boolean,
                indentation = style[IndentationDef.id] as Int,
                inlineIfBraceIfStatement = style[InlineIfBraceIfStatementDef.id] as Boolean,
            )

        fun fromPath(path: String): FormatterStyleConfig = fromMap(loadFromFile(File(path), RULES))
    }
}
