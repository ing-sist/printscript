package shared

import utils.RuleOwner
import utils.Type

interface RuleDefinition<out C : RuleConfig> {
    val id: String
    val description: String
    val owner: RuleOwner
    val default: C
    val type: Type

    fun parse(configMap: Map<String, String>): RuleConfig
}
