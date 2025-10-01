package impl.spaces

import Token
import TokenType
import config.FormatterStyleConfig
import config.RuleDef
import config.RuleRegistry
import config.SpaceAroundOperatorsDef
import impl.interfaces.SpaceAfterRule
import impl.interfaces.SpaceBeforeRule

object SpaceAroundOperators : SpaceBeforeRule, SpaceAfterRule {
    override val id: RuleDef<Boolean> = SpaceAroundOperatorsDef

    init {
        RuleRegistry.registerRule(this)
    }

    val operators = TokenType.Operator.all

    override fun spaceBefore(
        curr: Token,
        style: FormatterStyleConfig,
    ): Boolean? {
        var result: Boolean? = true
        if(style[SpaceAroundOperatorsDef] == false) return false
        if (curr.type !in operators) result = null
        return result
    }

    override fun spaceAfter(
        curr: Token,
        style: FormatterStyleConfig,
    ): Boolean? {
        var result: Boolean? = true
        if(style[SpaceAroundOperatorsDef] == false) return false
        if (curr.type !in operators) result = null
        return result
    }
}
