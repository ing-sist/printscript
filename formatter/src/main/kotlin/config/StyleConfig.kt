package config

import config.ConfigRegistry.RULES
import rules.definitions.IfStatementIndentationDef
import rules.definitions.InlineIfBraceIfStatementDef
import rules.definitions.LineBreakAfterSemiColonDef
import rules.definitions.LineBreakBeforePrintlnDef
import rules.definitions.Rule
import rules.definitions.SpaceAfterColonDef
import rules.definitions.SpaceAroundAsignementDef
import rules.definitions.SpaceAroundOperatorsDef
import rules.definitions.SpaceBeforeColonDef
import rules.definitions.SpaceBetweenTokensDef
import java.io.File

data class StyleConfig(
    val lineBreakBeforePrintln: Int,
    val lineBreakAfterSemicolon: Boolean,
    val spaceBeforeColon: Boolean,
    val spaceAfterColon: Boolean,
    val spaceAroundAssignment: Boolean,
    val spaceBetweenTokens: Boolean,
    val spaceAroundOperators: Boolean,
    val ifStatementIndentation: Int,
    val inlineIfBraceIfStatement: Boolean,
) {
    companion object {
        fun fromMap(style: Map<String, Any>): StyleConfig =
            StyleConfig(
                lineBreakBeforePrintln = style[LineBreakBeforePrintlnDef.id] as Int,
                lineBreakAfterSemicolon = style[LineBreakAfterSemiColonDef.id] as Boolean,
                spaceBeforeColon = style[SpaceBeforeColonDef.id] as Boolean,
                spaceAfterColon = style[SpaceAfterColonDef.id] as Boolean,
                spaceAroundAssignment = style[SpaceAroundAsignementDef.id] as Boolean,
                spaceBetweenTokens = style[SpaceBetweenTokensDef.id] as Boolean,
                spaceAroundOperators = style[SpaceAroundOperatorsDef.id] as Boolean,
                ifStatementIndentation = style[IfStatementIndentationDef.id] as Int,
                inlineIfBraceIfStatement = style[InlineIfBraceIfStatementDef.id] as Boolean,
            )

        fun fromPath(path: File): StyleConfig = fromMap(loadFromFile(path, RULES))
    }
}

object ConfigRegistry {
    val RULES: List<Rule<*>> =
        listOf(
            LineBreakBeforePrintlnDef,
            LineBreakAfterSemiColonDef,
            SpaceBeforeColonDef,
            SpaceAfterColonDef,
            SpaceAroundAsignementDef,
            SpaceBetweenTokensDef,
            SpaceAroundOperatorsDef,
            InlineIfBraceIfStatementDef,
            IfStatementIndentationDef,
        )
}
