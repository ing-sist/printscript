package rules.definitions

import config.RuleOwner

object KeywordDef : Rule<Boolean> {
    override val id: String = "keywords"
    override val default: Boolean = true
    override val owner: RuleOwner = RuleOwner.ENGINE

    override fun parse(raw: String): List<String> = raw.split(",").map { it.trim() }.filter { it.isNotEmpty() }
}
