package impl.newlines

import DocBuilder
import Token
import config.BraceLineBreakDef
import config.FormatterStyleConfig
import config.RuleDef
import config.RuleRegistry
import impl.interfaces.NewlineAfterRule

object BraceLineBreak : NewlineAfterRule {
    override val id: RuleDef<Int> = BraceLineBreakDef

    init {
        RuleRegistry.registerRule(this)
    }

    override fun newlineAfter(
        curr: Token,
        style: FormatterStyleConfig,
        out: DocBuilder,
    ): Int =
        when (curr.type) {
            is TokenType.RightBrace -> 1
            is TokenType.LeftBrace -> 1
            else -> 0
        }
}
