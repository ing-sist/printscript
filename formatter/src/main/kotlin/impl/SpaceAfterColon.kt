package impl

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

    override fun spaceAfter(
        curr: Token,
        style: FormatterStyleConfig,
    ): Boolean? {
        var result: Boolean? = true
        if(style[SpaceAfterColonDef] == false) return false
        if (curr.type !is TokenType.Colon) result = null
        return result
    }
}
