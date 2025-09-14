import shared.RuleConfig
import shared.RuleDefinition
import utils.RuleOwner
import java.io.File

fun loadAnalyzerFromFile(
    file: File,
    defs: List<RuleDefinition<RuleConfig>>,
): Map<String, RuleConfig> {
    val text = file.readText().trim()
    val inner = text.removePrefix("{").removeSuffix("}").trim()
    if (inner.isBlank()) return defs.associate { it.id to it.default }

    val raw = LinkedHashMap<String, String>()
    for (entry in inner.split(",")) {
        val parts = entry.split(":")
        require(parts.size == 2) { "Invalid input: $entry" }
        val key = parts[0].trim().removeSurrounding("\"")
        val value = parts[1].trim().removeSurrounding("\"")
        raw[key] = value
    }

    val result: MutableMap<String, RuleConfig> = LinkedHashMap()
    for (def in defs) {
        val v = raw[def.id]
        val cfg: RuleConfig =
            if (def.owner == RuleOwner.ENGINE) {
                def.default
            } else if (v == null) {
                def.default
            } else {
                def.parse(mapOf(def.id to v))
            }
        result[def.id] = cfg
    }
    return result
}
