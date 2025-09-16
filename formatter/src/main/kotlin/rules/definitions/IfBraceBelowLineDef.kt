package rules.definitions

import config.RuleOwner

object IfBraceBelowLineDef : Rule<Boolean> {
    override val id: String = "ifBraceBelowLine"
    override val default: Boolean = false
    override val owner: RuleOwner = RuleOwner.USER

    override fun parse(raw: String): Boolean = raw.trim().lowercase() == "true"
}
