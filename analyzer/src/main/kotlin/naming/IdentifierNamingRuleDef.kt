package naming

import shared.RuleDefinition

class IdentifierNamingRuleDef : RuleDefinition {
    override val id: String = "Naming.IdentifierStyle"
    override val description = "Identifiers must follow the configured naming style"
}
