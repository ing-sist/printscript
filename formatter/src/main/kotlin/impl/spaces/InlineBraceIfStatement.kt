package impl.spaces

import Token
import config.FormatterStyleConfig
import config.InlineBraceIfStatementIdDef
import config.RuleDef
import config.RuleRegistry
import impl.interfaces.SpaceBeforeRule

object InlineBraceIfStatement : SpaceBeforeRule {
    override val id: RuleDef<Boolean> = InlineBraceIfStatementIdDef

    init {
        RuleRegistry.registerRule(this)
    }

    override fun spaceBefore(
        curr: Token,
        style: FormatterStyleConfig,
    ): Boolean? {
        var result: Boolean? = true
        if(style[InlineBraceIfStatementIdDef] == false) return false
        if (curr.type !is TokenType.LeftBrace) result = null
        return result
    }
}