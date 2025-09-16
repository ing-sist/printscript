package config

import config.RuleDefinitions.RULES
import loadFromFile
import loadFromString
import rules.definitions.IndentationDef
import rules.definitions.InlineIfBraceIfStatementDef
import rules.definitions.LineBreakAfterPrintlnDef
import rules.definitions.LineBreakAfterSemiColonDef
import rules.definitions.SpaceAfterColonDef
import rules.definitions.SpaceAroundAssignmentDef
import rules.definitions.SpaceAroundOperatorsDef
import rules.definitions.SpaceBeforeColonDef
import java.io.File

data class FormatterStyleConfig(
    val lineBreakAfterPrintln: Int,
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
                lineBreakAfterPrintln = style[LineBreakAfterPrintlnDef.id] as Int,
                lineBreakAfterSemicolon = style[LineBreakAfterSemiColonDef.id] as Boolean,
                spaceBeforeColon = style[SpaceBeforeColonDef.id] as Boolean,
                spaceAfterColon = style[SpaceAfterColonDef.id] as Boolean,
                spaceAroundAssignment = style[SpaceAroundAssignmentDef.id] as Boolean,
                spaceAroundOperators = style[SpaceAroundOperatorsDef.id] as Boolean,
                indentation = style[IndentationDef.id] as Int,
                inlineIfBraceIfStatement = style[InlineIfBraceIfStatementDef.id] as Boolean,
            )

        fun default(): FormatterStyleConfig =
            FormatterStyleConfig(
                lineBreakAfterPrintln = 0,
                lineBreakAfterSemicolon = true,
                spaceBeforeColon = false,
                spaceAfterColon = true,
                spaceAroundAssignment = true,
                spaceAroundOperators = true,
                indentation = 4,
                inlineIfBraceIfStatement = false,
            )

        fun fromJson(json: String): FormatterStyleConfig = fromMap(loadFromString(json, RuleDefinitions.RULES))

        fun fromPath(path: String): FormatterStyleConfig = fromMap(loadFromFile(File(path), RULES))
    }
}
