package rules.definitions

import config.RuleOwner

object LineBreakAfterPrintlnDef : Rule<Int> {
    override val id: String = "lineBreakAfterPrintln"
    override val default: Int = 0
    override val owner: RuleOwner = RuleOwner.USER

    override fun parse(raw: String): Int = raw.trim().toInt() // result if > 2
}
