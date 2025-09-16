package rules.definitions

import config.RuleOwner

object InlineIfBraceIfStatementDef : Rule<Boolean> {
    override val id: String = "inlineIfBraceIfStatement"
    override val default: Boolean = false
    override val owner: RuleOwner = RuleOwner.ENGINE

    override fun parse(raw: String): Boolean = raw.trim().lowercase() == "true"
}
