package rules.definitions

import rules.RuleOwner

interface Rule<T : Any> {
    val id: String
    val default: T
    val owner: RuleOwner

    fun parse(raw: String): Any
}
