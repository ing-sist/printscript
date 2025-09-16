import config.AliasesMap.ALIASES
import config.RuleOwner
import rules.definitions.Rule
import java.io.File

fun loadStyleMapFromFile(
    file: File,
    rules: List<Rule<*>>,
): Map<String, Any> = styleMapFromText(file.readText(), rules) // solo las que me da el user

fun loadStyleMapFromString(
    json: String,
    rules: List<Rule<*>>,
): Map<String, Any> = styleMapFromText(json, rules) // solo las que me da el user

// ENGINE ∪ USER
fun activeRuleIdsFromString(
    json: String,
    rules: List<Rule<*>>,
): Set<String> {
    val inner =
        json
            .trim()
            .removePrefix("{")
            .removeSuffix("}")
            .trim()
    val userIds = if (inner.isBlank()) emptySet() else parseEntries(inner).keys
    val engineIds = rules.filter { it.owner == RuleOwner.ENGINE }.map { it.id }.toSet()
    return userIds + engineIds
}

fun activeRuleIdsFromFile(
    file: File,
    rules: List<Rule<*>>,
): Set<String> = activeRuleIdsFromString(file.readText(), rules)

/** Sólo devuelve claves USER provistas (parseadas). JSON vacío => {} */
private fun styleMapFromText(
    textRaw: String,
    rules: List<Rule<*>>,
): Map<String, Any> {
    val inner =
        textRaw
            .trim()
            .removePrefix("{")
            .removeSuffix("}")
            .trim()
    if (inner.isBlank()) return emptyMap()

    val raw = parseEntries(inner) // Map<idNormalizada, String>
    val defsById = rules.associateBy { it.id }

    val result = mutableMapOf<String, Any>()
    for ((id, rawVal) in raw) {
        val def = defsById[id] ?: continue // clave desconocida => ignorar
        result[id] = def.parse(rawVal)
    }
    return result
}

/** Aplica alias + transform; deja ids normalizadas como claves */
fun parseEntries(inner: String): Map<String, String> {
    fun clean(s: String) = s.trim().removeSurrounding("\"")

    return inner
        .splitToSequence(',')
        .map { it.trim() }
        .filter { it.isNotEmpty() }
        .map { e ->
            val idx = e.indexOf(':')
            require(idx > 0) { "Entrada inválida: $e" }
            val keyRaw = clean(e.substring(0, idx))
            val valueRaw = clean(e.substring(idx + 1))
            val alias = ALIASES[keyRaw]
            val targetId = alias?.targetId ?: keyRaw
            val value = alias?.transform?.invoke(valueRaw) ?: valueRaw
            targetId to value
        }.toMap()
}
