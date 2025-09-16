package rules.definitions

import config.RuleOwner
import config.registerDef
import config.registerImpl
import rules.implementations.LineBreakBeforePrintln

object LineBreakBeforePrintlnDef : Rule<Int> {
    override val id: String = "lineBreakBeforePrintln"
    override val default: Int = 0
    override val owner: RuleOwner = RuleOwner.USER

    override fun parse(raw: String): Int = raw.trim().toInt()

    init {
        registerDef(this)
        registerImpl(id, LineBreakBeforePrintln)
    }
}
