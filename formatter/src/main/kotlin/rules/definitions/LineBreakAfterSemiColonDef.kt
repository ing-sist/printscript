package rules.definitions

import config.RuleOwner

object LineBreakAfterSemiColonDef : Rule<Boolean> {
    override val id: String = "lineBreakAfterSemicolon"
    override val default: Boolean = true
    override val owner: RuleOwner = RuleOwner.ENGINE

    override fun parse(raw: String): Boolean = raw.trim().lowercase() == "true"
}
