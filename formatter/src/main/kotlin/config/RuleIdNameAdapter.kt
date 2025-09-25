package config

fun interface RuleIdNameAdapter {
    fun resolve(externalName: String): RuleDef<*>?
}
