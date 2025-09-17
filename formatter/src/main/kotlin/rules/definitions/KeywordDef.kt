package rules.definitions

import config.RuleOwner
import config.registerDef
import config.registerImpl
import rules.implementations.KeywordSpacing

object KeywordDef : Rule<Boolean> {
    override val id: String = "keywords"
    override val default: Boolean = true
    override val owner: RuleOwner = RuleOwner.ENGINE

    override fun parse(raw: String): List<String> = raw.split(",").map { it.trim() }.filter { it.isNotEmpty() }

    init {
        registerDef(this)
        registerImpl(id, KeywordSpacing)
    }
}
