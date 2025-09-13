package simple

import shared.RuleDefinition

interface SimpleArgDef : RuleDefinition {
    val restrictedCases: Set<String>
}
