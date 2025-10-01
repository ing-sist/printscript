package impl.spaces

import Token
import config.FormatterStyleConfig
import config.RuleDef
import config.RuleRegistry
import config.SpaceAfterColonDef
import impl.interfaces.SpaceAfterRule

object SpaceAfterColon : SpaceAfterRule {
    override val id: RuleDef<Boolean> = SpaceAfterColonDef

    init {
        RuleRegistry.registerRule(this)
    }


    override fun spaceAfter(curr: Token, style: FormatterStyleConfig): Boolean? {
        if (curr.type !is TokenType.Colon) return null
        return style[SpaceAfterColonDef]
    }
}
