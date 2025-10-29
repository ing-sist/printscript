package impl.newlines

import DocBuilder
import Token
import config.BelowLineBraceIfStatementDef
import config.FormatterStyleConfig
import config.RuleDef
import config.RuleRegistry
import impl.interfaces.NewlineAfterRule
import impl.interfaces.NewlineBeforeRule

object BelowLineBraceIfStatement : NewlineBeforeRule, NewlineAfterRule {
    override val id: RuleDef<Boolean> = BelowLineBraceIfStatementDef

    init {
        RuleRegistry.registerRule(this)
    }

    override fun newlineBefore(
        curr: Token,
        style: FormatterStyleConfig,
        out: DocBuilder,
    ): Int {
        var result = 0
        if (curr.type is TokenType.LeftBrace) result = 1
        return result
    }

    override fun newlineAfter(
        curr: Token,
        style: FormatterStyleConfig,
        out: DocBuilder,
    ): Int {
        var result = 0
        if (curr.type is TokenType.LeftBrace || curr.type is TokenType.RightBrace) result = 1
        return result
    }
}
