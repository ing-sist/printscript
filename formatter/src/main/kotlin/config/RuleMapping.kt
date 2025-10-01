package config

data class RuleMapping(val def: RuleDef<*>, val transform: (Any?) -> Any?)
