package config

object DefaultRuleAdapter : RuleIdNameAdapter {
    override fun resolve(name: String): RuleMapping? =
        when (name) {
            "SpaceAroundOperators"     -> RuleMapping(SpaceAroundOperatorsDef) {it}
            "enforce-spacing-around-equals"    -> RuleMapping(SpaceAroundAssignmentDef) { it}
            "enforce-no-spacing-around-equals"    -> RuleMapping(SpaceAroundAssignmentDef) { v ->
                when (v) { null -> null; is Boolean -> !v; else -> {}}
            }
            "Indentation"              -> RuleMapping(IndentationDef) {it}
            "KeywordSpacingAfter"      -> RuleMapping(KeywordSpacingAfterDef) {it}
            "line-breaks-after-println" -> RuleMapping(LineBreakBeforePrintlnDef) {it}
            "mandatory-space-surrounding-operations" -> RuleMapping(SpaceAroundOperatorsDef) {it}
            "enforce-spacing-before-colon-in-declaration" -> RuleMapping(SpaceBeforeColonDef) {it}
            "enforce-spacing-after-colon-in-declaration" -> RuleMapping(SpaceAfterColonDef) {it}
            "if-brace-below-line" -> RuleMapping(BelowLineBraceIfStatementDef) {it}
            "if-brace-same-line" -> RuleMapping(InlineBraceIfStatementIdDef) {it}
            "indent-inside-if" -> RuleMapping(IndentationDef) {it}
            "mandatory-line-break-after-statement" -> RuleMapping(LineBreakAfterSemiColonDef) {it}
            "mandatory-single-space-separation" -> RuleMapping(MaxSpaceBetweenTokensDef) {it}
            else -> null
        }
}