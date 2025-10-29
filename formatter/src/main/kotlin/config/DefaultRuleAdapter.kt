package config

object DefaultRuleAdapter : RuleIdNameAdapter {

    private val mappings: Map<String, (Any?) -> RuleMapping> = mapOf(
        "SpaceAroundOperators" to { _ -> RuleMapping(SpaceAroundOperatorsDef) { it } },
        "enforce-spacing-around-equals" to { _ -> RuleMapping(SpaceAroundAssignmentDef) { it } },
        "Indentation" to { _ -> RuleMapping(IndentationDef) { it } },
        "KeywordSpacingAfter" to { _ -> RuleMapping(KeywordSpacingAfterDef) { it } },
        "line-breaks-after-println" to { _ -> RuleMapping(LineBreakBeforePrintlnDef) { it } },
        "mandatory-space-surrounding-operations" to { _ -> RuleMapping(SpaceAroundOperatorsDef) { it } },
        "enforce-spacing-before-colon-in-declaration" to { _ -> RuleMapping(SpaceBeforeColonDef) { it } },
        "enforce-spacing-after-colon-in-declaration" to { _ -> RuleMapping(SpaceAfterColonDef) { it } },
        "if-brace-below-line" to { _ -> RuleMapping(BelowLineBraceIfStatementDef) { it } },
        "if-brace-same-line" to { _ -> RuleMapping(InlineBraceIfStatementIdDef) { it } },
        "indent-inside-if" to { _ -> RuleMapping(IndentationDef) { it } },
        "mandatory-line-break-after-statement" to { _ -> RuleMapping(LineBreakAfterSemiColonDef) { it } },
        "mandatory-single-space-separation" to { _ -> RuleMapping(MaxSpaceBetweenTokensDef) { it } },

        // 2: casos especiales
        "enforce-no-spacing-around-equals" to { _ ->
            RuleMapping(SpaceAroundAssignmentDef) { v ->
                when (v) {
                    null -> null
                    is Boolean -> !v
                    else -> { /* ignore / default */ }
                }
            }
        }
    )

    override fun resolve(name: String): RuleMapping? =
        mappings[name]?.invoke(null)
}

//object DefaultRuleAdapter : RuleIdNameAdapter {
//    override fun resolve(name: String): RuleMapping? =
//        when (name) {
//            "SpaceAroundOperators" -> RuleMapping(SpaceAroundOperatorsDef) { it }
//            "enforce-spacing-around-equals" -> RuleMapping(SpaceAroundAssignmentDef) { it }
//            "enforce-no-spacing-around-equals" ->
//                RuleMapping(SpaceAroundAssignmentDef) { v ->
//                    when (v) {
//                        null -> null
//                        is Boolean -> !v
//                        else -> {}
//                    }
//                }
//            "Indentation" -> RuleMapping(IndentationDef) { it }
//            "KeywordSpacingAfter" -> RuleMapping(KeywordSpacingAfterDef) { it }
//            "line-breaks-after-println" -> RuleMapping(LineBreakBeforePrintlnDef) { it }
//            "mandatory-space-surrounding-operations" -> RuleMapping(SpaceAroundOperatorsDef) { it }
//            "enforce-spacing-before-colon-in-declaration" -> RuleMapping(SpaceBeforeColonDef) { it }
//            "enforce-spacing-after-colon-in-declaration" -> RuleMapping(SpaceAfterColonDef) { it }
//            "if-brace-below-line" -> RuleMapping(BelowLineBraceIfStatementDef) { it }
//            "if-brace-same-line" -> RuleMapping(InlineBraceIfStatementIdDef) { it }
//            "indent-inside-if" -> RuleMapping(IndentationDef) { it }
//            "mandatory-line-break-after-statement" -> RuleMapping(LineBreakAfterSemiColonDef) { it }
//            "mandatory-single-space-separation" -> RuleMapping(MaxSpaceBetweenTokensDef) { it }
//            else -> null
//        }
//}
