package shared

import PrintlnSimpleArgDef
import ReadInputSimpleArgDef
import naming.IdentifierNamingRuleDef

object AnalyzerRuleDefinitions {
    val RULES: List<RuleDefinition<RuleConfig>> =
        listOf(
            IdentifierNamingRuleDef,
            PrintlnSimpleArgDef,
            ReadInputSimpleArgDef,
        )
}
