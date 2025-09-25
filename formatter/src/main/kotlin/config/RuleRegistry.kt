package config

import impl.interfaces.Rule

object RuleRegistry {
    private val defs = linkedSetOf<RuleDef<*>>()
    private val defsById = linkedMapOf<String, RuleDef<*>>()

    private fun registerDef(def: RuleDef<*>) {
        if (defs.add(def)) defsById[def.id] = def
    }

    fun allDefs(): List<RuleDef<*>> = defs.toList()
    fun resolveDef(id: String): RuleDef<*>? = defsById[id]

    private val rules = linkedSetOf<Rule>()

    fun registerRule(rule: Rule) {
        registerDef(rule.id)
        rules += rule
    }

    fun allRules(): List<Rule> {
        return rules.toList()
    }
}