package config

//class FormatterStyleConfig(
//    private val values: Map<RuleDef<*>, Any> = emptyMap(),
//) {
//    @Suppress("UNCHECKED_CAST")
//    operator fun <T> get(key: RuleDef<T>): T? = (values[key] as? T) ?: key.default
//
//    fun <T> contains(key: RuleDef<T>): Boolean = values.containsKey(key)
//
//    fun keys(): Set<RuleDef<*>> = values.keys
//
//    fun merge(override: FormatterStyleConfig): FormatterStyleConfig = FormatterStyleConfig(values + override.values)
//
//}

class FormatterStyleConfig(
    /** Valores que vinieron en el JSON mapeados por RuleDef.
     *  Puede contener null (=> “apagada explícitamente”). */
    private val explicit: Map<RuleDef<*>, Any?> = emptyMap(),
) {
    @Suppress("UNCHECKED_CAST")
    fun <T> valueOrNull(def: RuleDef<T>): T? {
        // Si está explícito (aunque sea null), usá eso; sino, caé en default
        val v = explicit[def]
        return (v as? T) ?: def.default
    }

    @Suppress("UNCHECKED_CAST")
    operator fun <T> get(def: RuleDef<T>): T? = valueOrNull(def)

    fun isExplicit(def: RuleDef<*>): Boolean = def in explicit

    /** Regla activa si:
     *   - está en JSON y su valor != null, o
     *   - no está en JSON y su default != null
     */
    fun isActive(def: RuleDef<*>): Boolean =
        if (isExplicit(def)) explicit[def] != null else def.default != null

    /** Ids activas según el criterio anterior. */
    fun activeDefs(): Set<RuleDef<*>> =
        buildSet {
            // 1) Explícitas con valor no-nulo
            explicit.filterValues { it != null }.keys.forEach { add(it) }
            // 2) Defaults no-nulos que no fueron sobrescritos en JSON
            RuleRegistry.allDefs()
                .filter { it !in explicit && it.default != null }
                .forEach { add(it) }
        }

    /** Merge conservando nulls explícitos de override. */
    fun merge(override: FormatterStyleConfig): FormatterStyleConfig =
        FormatterStyleConfig(explicit + override.explicit)
}
