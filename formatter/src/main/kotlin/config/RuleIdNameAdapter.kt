package config

fun interface RuleIdNameAdapter {
    fun resolve(name: String): RuleMapping?
}