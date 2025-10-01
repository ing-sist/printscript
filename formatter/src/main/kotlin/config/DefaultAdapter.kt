package config

object DefaultRuleAdapter : RuleIdNameAdapter {
    override fun resolve(externalName: String): RuleDef<*>? =
        when (externalName) {
            "SpaceAroundOperators"     -> SpaceAroundOperatorsDef
            "enforce-spacing-around-equals"    -> SpaceAroundAssignmentDef
            "enforce-no-spacing-around-equals"    -> SpaceAroundAssignmentDef
            "Indentation"              -> IndentationDef
            "KeywordSpacingAfter"      -> KeywordSpacingAfterDef
            "line-breaks-after-println" -> LineBreakBeforePrintlnDef
            "mandatory-space-surrounding-operations" -> SpaceAroundOperatorsDef
            "enforce-spacing-before-colon-in-declaration" -> SpaceBeforeColonDef
            "enforce-spacing-after-colon-in-declaration" -> SpaceAfterColonDef
            "if-brace-below-line" -> BelowLineBraceIfStatementDef
            "if-brace-same-line" -> InlineBraceIfStatementIdDef
            "indent-inside-if" -> IndentationDef
            "mandatory-line-break-after-statement" -> LineBreakAfterSemiColonDef
            "mandatory-single-space-separation" -> MaxSpaceBetweenTokensDef
            else -> null
        }
}