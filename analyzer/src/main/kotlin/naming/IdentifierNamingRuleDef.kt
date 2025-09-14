package naming

import shared.RuleDefinition
import utils.RuleOwner
import utils.Type

object IdentifierNamingRuleDef : RuleDefinition<IdentifierNamingConfig> {
    override val id: String = "identifierNamingType"
    override val description = "Identifiers must follow the configured naming style"
    override val default: IdentifierNamingConfig =
        IdentifierNamingConfig(IdentifierCase.CAMEL_CASE, true)
    override val owner: RuleOwner = RuleOwner.USER
    override val type: Type = Type.WARNING

    override fun parse(configMap: Map<String, String>): IdentifierNamingConfig {
        val input: String? = configMap["identifierNamingType"] ?: return default
        return when (input?.trim()?.lowercase()) {
            "camel" -> IdentifierNamingConfig(IdentifierCase.CAMEL_CASE)
            "snake" -> IdentifierNamingConfig(IdentifierCase.SNAKE_CASE)
            else -> default
        }
    }
}
