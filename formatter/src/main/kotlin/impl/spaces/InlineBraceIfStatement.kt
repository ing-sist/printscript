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
        if (curr.type !is TokenType.LeftBrace) return null
        return style[InlineBraceIfStatementIdDef]
    }
}
