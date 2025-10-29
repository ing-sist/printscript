package config

class FormatterStyleConfig(
    private val explicit: Map<RuleDef<*>, Any?> = emptyMap(),
) {
    @Suppress("UNCHECKED_CAST")
    fun <T> valueOrNull(def: RuleDef<T>): T? {
        val v = explicit[def]
        return (v as? T) ?: def.default
    }

    @Suppress("UNCHECKED_CAST")
    operator fun <T> get(def: RuleDef<T>): T? = valueOrNull(def)

    fun isExplicit(def: RuleDef<*>): Boolean = def in explicit

    fun isActive(def: RuleDef<*>): Boolean = if (isExplicit(def)) explicit[def] != null else def.default != null

    fun activeDefs(): Set<RuleDef<*>> =
        buildSet {
            explicit.filterValues { it != null }.keys.forEach { add(it) }
            RuleRegistry
                .allDefs()
                .filter { it !in explicit && it.default != null }
                .forEach { add(it) }
        }

    fun merge(override: FormatterStyleConfig): FormatterStyleConfig = FormatterStyleConfig(explicit + override.explicit)
}
