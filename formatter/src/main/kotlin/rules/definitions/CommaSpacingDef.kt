package rules.definitions

import config.RuleOwner
import config.registerDef
import config.registerImpl
import rules.implementations.CommaSpacing

object CommaSpacingDef : Rule<Boolean> {
    override val id: String = "commaSpacing"
    override val default: Boolean = true
    override val owner: RuleOwner = RuleOwner.ENGINE

    override fun parse(raw: String): Boolean = raw.trim().toBoolean()

    init {
        registerDef(this)
        registerImpl(id, CommaSpacing)
    }
}
