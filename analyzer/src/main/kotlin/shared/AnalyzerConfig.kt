import shared.RuleConfig
import shared.RuleDefinition
import java.io.File

class AnalyzerConfig(
    private val map: Map<String, RuleConfig>,
) {
    @Suppress("UNCHECKED_CAST")
    fun <C : RuleConfig> get(def: RuleDefinition<C>): C =
        map[def.id] as C?
            ?: error("Missing config for rule '${def.id}'")

    @Suppress("UNCHECKED_CAST")
    fun <C : RuleConfig> tryGet(def: RuleDefinition<C>): C? = map[def.id] as C?

    companion object {
        fun fromPath(
            path: String,
            defs: List<RuleDefinition<out RuleConfig>>,
        ): AnalyzerConfig = AnalyzerConfig(loadAnalyzerFromFile(File(path), defs))
    }
}
