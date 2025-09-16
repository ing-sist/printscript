package rules.definitions

import config.RuleOwner

object IndentationDef : Rule<Int> {
    override val id: String = "indentation"
    override val default: Int = 2
    override val owner: RuleOwner = RuleOwner.USER

    override fun parse(raw: String): Int = raw.trim().toInt()
}
