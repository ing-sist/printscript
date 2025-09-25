package impl

import DocBuilder
import Token
import config.FormatterStyleConfig
import config.LineBreakAfterSemiColonDef
import config.RuleDef
import config.RuleRegistry
import impl.interfaces.NewlineAfterRule

object LineBreakAfterSemiColon : NewlineAfterRule {
    override val id: RuleDef<Boolean> = LineBreakAfterSemiColonDef

    init {
        RuleRegistry.registerRule(this)
    }

    override fun newlineAfter(
        curr: Token,
        style: FormatterStyleConfig,
        out: DocBuilder,
    ): Int {
        var result = 0
        if (curr.type is TokenType.Semicolon) result = 1
        return result
    }
}
