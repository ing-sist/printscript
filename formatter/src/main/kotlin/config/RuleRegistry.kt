// RuleRegistry.kt
package config

import parseEntries
import rules.definitions.Rule
import rules.implementations.RuleImplementation
import java.io.File
import java.util.LinkedHashMap

// --- NUEVO: mapas mutables internos ---
private val defsMutable = LinkedHashMap<String, Rule<*>>()
private val implsMutable = LinkedHashMap<String, RuleImplementation>()

// Exposición solo de lectura
val RULE_DEFS_BY_ID: Map<String, Rule<*>> get() = defsMutable
val RULE_TO_IMPL: Map<String, RuleImplementation> get() = implsMutable

// --- NUEVO: APIs de autoregistro ---
fun registerDef(def: Rule<*>) {
    defsMutable[def.id] = def
}

fun registerImpl(
    id: String,
    impl: RuleImplementation,
) {
    implsMutable[id] = impl
}

// --- Helpers públicos ---
fun allRuleDefs(): List<Rule<*>> = RULE_DEFS_BY_ID.values.toList()

fun selectImplementations(activeIds: Set<String>): List<RuleImplementation> = activeIds.mapNotNull { RULE_TO_IMPL[it] }

// --- Activación SIN pasar defs ---
fun activeImplementationsFromJson(json: String): List<RuleImplementation> {
    val engineIds =
        RULE_DEFS_BY_ID.values
            .filter { it.owner == RuleOwner.ENGINE }
            .map { it.id }
            .toSet()

    val inner =
        json
            .trim()
            .removePrefix("{")
            .removeSuffix("}")
            .trim()
    val userIds =
        if (inner.isBlank()) {
            emptySet()
        } else {
            parseEntries(inner).keys.toSet() // ya normaliza aliases
        }

    val active = engineIds + userIds
    return selectImplementations(active).distinct()
}

fun activeImplementationsFromFile(file: File): List<RuleImplementation> = activeImplementationsFromJson(file.readText())

fun activeImplementationsFromPath(path: String): List<RuleImplementation> = activeImplementationsFromFile(File(path))
