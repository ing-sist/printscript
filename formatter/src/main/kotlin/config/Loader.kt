// package config
//
// import rules.definitions.Rule
// import java.io.File
//
// // { JSON
// //    "SpaceBeforeColon": false,
// // }
//
// fun loadFromFile(
//    file: File,
//    rules: List<Rule<*>>,
// ): Map<String, Any> {
//    val text = file.readText().trim()
//
//    // saco { llaves
//    val inner = text.removePrefix("{").removeSuffix("}").trim()
//    if (inner.isBlank()) return rules.associate { it.id to it.default }
//
//    // separo por :
//    val entries = inner.split(",")
//    val raw = mutableMapOf<String, String>()
//
//    for (e in entries) {
//        val parts = e.split(":")
//        if (parts.size != 2) error("Entrada inválida: $e")
//
//
//        val key = parts[0].trim().removeSurrounding("\"")
//        val value = parts[1].trim()
//        raw[key] = value
//    }
//
//    val result = mutableMapOf<String, Any>()
//    for (rule in rules) {
//        if (rule.owner == RuleOwner.ENGINE) { // si es del engine, pongo la que tiene la rule
//            result[rule.id] = rule.default
//        } else { // si no, pongo la del user
//            val inputValue = raw[rule.id]
//            result[rule.id] = if (inputValue != null) rule.parse(inputValue) else rule.default
//        }
//    }
//
//    return result
// }
//
// fun loadFromString(
//    json: String,
//    rules: List<Rule<*>>,
// ): Map<String, Any> {
//    val text = json.trim()
//
//    val inner = text.removePrefix("{").removeSuffix("}").trim()
//    if (inner.isBlank()) return rules.associate { it.id to it.default }
//
//    val entries = inner.split(",")
//    val raw = mutableMapOf<String, String>()
//    for (e in entries) {
//        val parts = e.split(":")
//        require(parts.size == 2) { "Entrada inválida: $e" }
//        val key = parts[0].trim().removeSurrounding("\"")
//        val value = parts[1].trim()
//        raw[key] = value
//    }
//
//    val result = mutableMapOf<String, Any>()
//    for (rule in rules) {
//        if (rule.owner == RuleOwner.ENGINE) {
//            result[rule.id] = rule.default
//        } else {
//            val inputValue = raw[rule.id]
//            result[rule.id] = if (inputValue != null) rule.parse(inputValue) else rule.default
//        }
//    }
//    return result
// }

import config.AliasesMap.ALIASES
import config.RuleOwner
import rules.definitions.Rule
import java.io.File

fun loadFromFile(
    file: File,
    rules: List<Rule<*>>,
): Map<String, Any> = parseText(file.readText(), rules)

fun loadFromString(
    json: String,
    rules: List<Rule<*>>,
): Map<String, Any> = parseText(json, rules)

private fun parseText(
    textRaw: String,
    rules: List<Rule<*>>,
): Map<String, Any> {
    val text = textRaw.trim()
    val inner = text.removePrefix("{").removeSuffix("}").trim()

    // Si está vacío, devolvemos todos los defaults
    if (inner.isBlank()) return rules.associate { it.id to it.default }

    val raw = parseEntries(inner)

    // Construimos el resultado: para cada regla, tomamos valor normalizado si está,
    // si no, default. Las reglas del ENGINE siempre van con su default.
    val result = mutableMapOf<String, Any>()
    for (rule in rules) {
        val inputValue = raw[rule.id]
        val value =
            if (rule.owner == RuleOwner.ENGINE) {
                rule.default
            } else if (inputValue != null) {
                rule.parse(inputValue)
            } else {
                rule.default
            }
        println("APPLY ${rule.id} owner=${rule.owner} <- '$inputValue' => $value (default=${rule.default})")
        result[rule.id] = value
    }
    return result
}

private fun parseEntries(inner: String): MutableMap<String, String> {
    val entries =
        inner
            .splitToSequence(',')
            .map { it.trim() }
            .filter { it.isNotEmpty() }

    fun clean(s: String) = s.trim().removeSurrounding("\"")

    val raw = mutableMapOf<String, String>()
    for (e in entries) {
        val idx = e.indexOf(':')
        require(idx > 0) { "Entrada inválida: $e" }

        val keyRaw = clean(e.substring(0, idx))
        val valueRaw = clean(e.substring(idx + 1))

        val alias = ALIASES[keyRaw]
        val targetId = alias?.targetId ?: keyRaw
        val value = alias?.transform?.invoke(valueRaw) ?: valueRaw

        raw[targetId] = value
    }
    return raw
}
