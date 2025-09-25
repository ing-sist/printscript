package impl

import DocBuilder
import Token
import TokenType
import config.FormatterStyleConfig
import config.IndentationDef
import config.RuleDef
import config.RuleRegistry
import impl.interfaces.NewlineAfterRule
import impl.interfaces.NewlineBeforeRule

object Indentation : NewlineBeforeRule, NewlineAfterRule {
    override val id: RuleDef<Int> = IndentationDef

    init {
        RuleRegistry.registerRule(this)
    }

    override fun newlineBefore(curr: Token, style: FormatterStyleConfig, out: DocBuilder): Int {
        var result = 1
        if (curr.type !is TokenType.RightBrace) result = 0
        return result
    }

    override fun newlineAfter(
        curr: Token,
        style: FormatterStyleConfig,
        out: DocBuilder,
    ): Int {
        var result = 4
        if (curr.type !is TokenType.LeftBrace) result = 0
        return result
    }
}
