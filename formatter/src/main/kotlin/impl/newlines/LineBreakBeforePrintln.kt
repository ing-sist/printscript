package impl.newlines

import DocBuilder
import Token
import config.FormatterStyleConfig
import config.LineBreakBeforePrintlnDef
import config.RuleDef
import config.RuleRegistry
import impl.interfaces.NewlineBeforeRule

object LineBreakBeforePrintln : NewlineBeforeRule {
    override val id: RuleDef<Int> = LineBreakBeforePrintlnDef

    init {
        RuleRegistry.registerRule(this)
    }

    override fun newlineBefore(curr: Token, style: FormatterStyleConfig, out: DocBuilder): Int {
        val lineBreakQuantity: Int? = style[id]
        if (curr.lexeme.lowercase() == "println") {
            return lineBreakQuantity ?: 0
        }
        return 0
    }
}
