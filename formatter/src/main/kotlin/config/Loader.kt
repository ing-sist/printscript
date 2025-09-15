package config

import rules.definitions.Rule
import java.io.File

// { JSON
//    "SpaceBeforeColon": false,
// }

fun loadFromFile(
    file: File,
    rules: List<Rule<*>>,
): Map<String, Any> {
    val text = file.readText().trim()

    // saco { llaves
    val inner = text.removePrefix("{").removeSuffix("}").trim()
    if (inner.isBlank()) return rules.associate { it.id to it.default }

    // separo por :
    val entries = inner.split(",")
    val raw = mutableMapOf<String, String>()

    for (e in entries) {
        val parts = e.split(":")
        if (parts.size != 2) error("Entrada inválida: $e")

        val key = parts[0].trim().removeSurrounding("\"")
        val value = parts[1].trim()
        raw[key] = value
    }

    val result = mutableMapOf<String, Any>()
    for (rule in rules) {
        if (rule.owner == RuleOwner.ENGINE) { // si es del engine, pongo la que tiene la rule
            result[rule.id] = rule.default
        } else { // si no, pongo la del user
            val inputValue = raw[rule.id]
            result[rule.id] = if (inputValue != null) rule.parse(inputValue) else rule.default
        }
    }

    return result
}

fun loadFromString(
    json: String,
    rules: List<Rule<*>>,
): Map<String, Any> {
    val text = json.trim()

    val inner = text.removePrefix("{").removeSuffix("}").trim()
    if (inner.isBlank()) return rules.associate { it.id to it.default }

    val entries = inner.split(",")
    val raw = mutableMapOf<String, String>()
    for (e in entries) {
        val parts = e.split(":")
        require(parts.size == 2) { "Entrada inválida: $e" }
        val key = parts[0].trim().removeSurrounding("\"")
        val value = parts[1].trim()
        raw[key] = value
    }

    val result = mutableMapOf<String, Any>()
    for (rule in rules) {
        if (rule.owner == RuleOwner.ENGINE) {
            result[rule.id] = rule.default
        } else {
            val inputValue = raw[rule.id]
            result[rule.id] = if (inputValue != null) rule.parse(inputValue) else rule.default
        }
    }
    return result
}
