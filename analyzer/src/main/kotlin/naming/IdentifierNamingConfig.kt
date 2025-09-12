package naming

import Type
import shared.RuleConfig

class IdentifierNamingConfig(
    override val enabled: Boolean = true,
    override val type: Type = Type.WARNING,
    val namingType: IdentifierCase = IdentifierCase.SNAKE_CASE,
) : RuleConfig
