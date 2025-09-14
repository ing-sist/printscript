import shared.RuleConfig
import shared.RuleDefinition
import java.io.File

class AnalyzerConfig(
    private val map: Map<String, RuleConfig>,
) {
    @Suppress("UNCHECKED_CAST")
    fun <C : RuleConfig> get(def: RuleDefinition<C>): C = map[def.id] as C

    companion object {
        fun fromPath(
            path: String,
            defs: List<RuleDefinition<RuleConfig>>,
        ): AnalyzerConfig = AnalyzerConfig(loadAnalyzerFromFile(File(path), defs))
    }
}
