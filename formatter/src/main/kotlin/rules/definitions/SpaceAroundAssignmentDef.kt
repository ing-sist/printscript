package rules.definitions

import config.RuleOwner

object SpaceAroundAssignmentDef : Rule<Boolean> {
    override val id: String = "spaceAroundAssignment"
    override val default: Boolean = false
    override val owner: RuleOwner = RuleOwner.USER

    override fun parse(raw: String): Boolean = raw.trim().lowercase() == "true"
}
