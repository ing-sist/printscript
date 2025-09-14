package rules.definitions

import config.RuleOwner

object SpaceBeforeColonDef : Rule<Boolean> {
    override val id: String = "spaceBeforeColon"
    override val default: Boolean = true
    override val owner: RuleOwner = RuleOwner.USER

    override fun parse(raw: String): Boolean = raw.trim().lowercase() == "true"
}
