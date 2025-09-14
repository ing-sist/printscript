package rules.definitions

import config.RuleOwner

object SpaceAfterColonDef : Rule<Boolean> {
    override val id: String = "spaceAfterColon"
    override val default: Boolean = true
    override val owner: RuleOwner = RuleOwner.USER

    override fun parse(raw: String): Boolean = raw.trim().lowercase() == "true"
}
