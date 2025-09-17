package rules.definitions

import config.RuleOwner
import config.registerDef
import config.registerImpl
import rules.implementations.LineBreakAfterSemicolon

object LineBreakAfterSemiColonDef : Rule<Boolean> {
    override val id: String = "lineBreakAfterSemicolon"
    override val default: Boolean = true
    override val owner: RuleOwner = RuleOwner.ENGINE

    override fun parse(raw: String): Boolean = raw.trim().lowercase() == "true"

    init {
        registerDef(this)
        registerImpl(id, LineBreakAfterSemicolon)
    }
}
