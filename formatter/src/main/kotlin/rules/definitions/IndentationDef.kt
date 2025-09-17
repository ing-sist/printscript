package rules.definitions

import Indentation
import config.RuleOwner
import config.registerDef
import config.registerImpl

object IndentationDef : Rule<Int> {
    override val id: String = "indentation"
    override val default: Int = 2
    override val owner: RuleOwner = RuleOwner.USER

    override fun parse(raw: String): Int = raw.trim().toInt()

    init {
        registerDef(this)
        registerImpl(id, Indentation)
    }
}
