package rules.definitions

import InlineBraceIfStatement
import config.RuleOwner
import config.registerDef
import config.registerImpl

object InlineIfBraceIfStatementDef : Rule<Boolean> {
    override val id: String = "inlineIfBraceIfStatement"
    override val default: Boolean = false
    override val owner: RuleOwner = RuleOwner.ENGINE

    override fun parse(raw: String): Boolean = raw.trim().lowercase() == "true"

    init {
        registerDef(this)
        registerImpl(id, InlineBraceIfStatement)
    }
}
