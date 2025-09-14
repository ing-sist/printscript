package simple

import shared.RuleDefinition

interface SimpleArgDef : RuleDefinition<SimpleArgConfig> {
    val restrictedCases: Set<String>
}
