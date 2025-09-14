package rules.definitions

import config.RuleOwner

interface Rule<T : Any> {
    val id: String
    val default: T
    val owner: RuleOwner

    fun parse(raw: String): Any
}
