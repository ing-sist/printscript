package impl.spaces

import Token
import config.FormatterStyleConfig
import config.RuleDef
import config.RuleRegistry
import config.SpaceBeforeColonDef
import impl.interfaces.SpaceBeforeRule

object SpaceBeforeColon : SpaceBeforeRule {
    override val id: RuleDef<Boolean> = SpaceBeforeColonDef

    init {
        RuleRegistry.registerRule(this)
    }

    override fun spaceBefore(
        curr: Token,
        style: FormatterStyleConfig,
    ): Boolean? {
        if (curr.type !is TokenType.Colon) return null
        return style[SpaceBeforeColonDef]
    }
}
