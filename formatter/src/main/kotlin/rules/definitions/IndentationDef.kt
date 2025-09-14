package rules.definitions

import config.RuleOwner

object IndentationDef : Rule<Int> {
    override val id: String = "indentation"
    override val default: Int = 4
    override val owner: RuleOwner = RuleOwner.USER

    override fun parse(raw: String): Int = raw.trim().toInt()
}
