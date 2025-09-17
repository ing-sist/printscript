package rules.definitions

import config.RuleOwner
import config.registerDef
import config.registerImpl
import rules.implementations.SpaceBetweenTokens

object SpaceBetweenTokensDef : Rule<Boolean> {
    override val id: String = "spaceBetweenTokens"
    override val default: Boolean = false
    override val owner: RuleOwner = RuleOwner.USER

    override fun parse(raw: String): Boolean = raw.trim().lowercase() == "true"

    init {
        registerDef(this)
        registerImpl(id, SpaceBetweenTokens)
    }
}
