package simple

import Type
import shared.RuleConfig

class SimpleArgConfig(
    override val enabled: Boolean,
    override val type: Type,
) : RuleConfig
