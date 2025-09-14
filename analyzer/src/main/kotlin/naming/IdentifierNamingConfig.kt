package naming

import shared.RuleConfig

class IdentifierNamingConfig(
    val namingType: IdentifierCase,
    override val enabled: Boolean = true,
) : RuleConfig
