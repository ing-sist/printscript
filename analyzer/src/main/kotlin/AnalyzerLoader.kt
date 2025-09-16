import shared.RuleConfig
import shared.RuleDefinition
import utils.RuleOwner
import java.io.File
import java.util.LinkedHashMap

fun loadAnalyzerFromFile(
    file: File,
    defs: List<RuleDefinition<out RuleConfig>>,
): Map<String, RuleConfig> {
    val text = file.readText().trim()
    val inner = text.removePrefix("{").removeSuffix("}").trim()

    // If empty JSON, no rules (no defaults).
    if (inner.isBlank()) return emptyMap()

    val raw = LinkedHashMap<String, String>()
    for (entry in inner.split(",")) {
        if (entry.isBlank()) continue
        val parts = entry.split(":")
        require(parts.size == 2) { "Invalid input: $entry" }
        val key = parts[0].trim().removeSurrounding("\"")
        val value = parts[1].trim().removeSurrounding("\"")
        raw[key] = value
    }

    // Aliases for backward compatibility / TCK mapping.
    // Key: canonical def.id; Value: accepted alternative keys in config JSON.
    val keyAliases: Map<String, List<String>> =
        mapOf(
            "identifierNamingType" to listOf("identifierNamingStyle"),
        )

    val result: MutableMap<String, RuleConfig> = LinkedHashMap()
    for (def in defs) {
        val candidates: List<String> = listOf(def.id) + (keyAliases[def.id] ?: emptyList())
        val foundKey = candidates.firstOrNull { raw.containsKey(it) } ?: continue
        val v = raw[foundKey]!!

        // Parse as if it came under the canonical id, so RuleDefinition.parse works unchanged.
        val cfg: RuleConfig =
            if (def.owner == RuleOwner.ENGINE) {
                def.parse(mapOf(def.id to v))
            } else {
                def.parse(mapOf(def.id to v))
            }
        result[def.id] = cfg
    }
    return result
}
