package config

import config.RuleDefinitions.RULES
import loadStyleMapFromFile
import loadStyleMapFromString
import rules.definitions.IfBraceBelowLineDef
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
    val ifBraceBelowLine: Boolean,
) {
    companion object {
        fun fromMap(style: Map<String, Any>): FormatterStyleConfig {
            val d = default()

            fun bool(
                key: String,
                fallback: Boolean,
            ): Boolean = (style[key] as? Boolean) ?: fallback

            fun int(
                key: String,
                fallback: Int,
            ): Int = (style[key] as? Number)?.toInt() ?: fallback

            return FormatterStyleConfig(
                lineBreakBeforePrintln = int(LineBreakBeforePrintlnDef.id, d.lineBreakBeforePrintln),
                lineBreakAfterSemicolon = bool(LineBreakAfterSemiColonDef.id, d.lineBreakAfterSemicolon),
                spaceBeforeColon = bool(SpaceBeforeColonDef.id, d.spaceBeforeColon),
                spaceAfterColon = bool(SpaceAfterColonDef.id, d.spaceAfterColon),
                spaceAroundAssignment = bool(SpaceAroundAssignmentDef.id, d.spaceAroundAssignment),
                spaceAroundOperators = bool(SpaceAroundOperatorsDef.id, d.spaceAroundOperators),
                indentation = int(IndentationDef.id, d.indentation),
                ifBraceBelowLine = bool(IfBraceBelowLineDef.id, d.ifBraceBelowLine),
                inlineIfBraceIfStatement = bool(InlineIfBraceIfStatementDef.id, d.inlineIfBraceIfStatement),
            )
        }

        fun default(): FormatterStyleConfig =
            FormatterStyleConfig(
                lineBreakBeforePrintln = 0,
                lineBreakAfterSemicolon = true,
                spaceBeforeColon = false,
                spaceAfterColon = true,
                spaceAroundAssignment = true,
                spaceAroundOperators = true,
                indentation = 4,
                ifBraceBelowLine = false,
                inlineIfBraceIfStatement = false,
            )

        fun fromJson(json: String): FormatterStyleConfig = fromMap(loadStyleMapFromString(json, RULES))

        fun fromPath(path: String): FormatterStyleConfig = fromMap(loadStyleMapFromFile(File(path), RULES))
    }
}
