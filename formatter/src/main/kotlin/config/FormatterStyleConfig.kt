package config

class FormatterStyleConfig(
    private val values: Map<RuleDef<*>, Any> = emptyMap(),
) {
    @Suppress("UNCHECKED_CAST")
    operator fun <T> get(key: RuleDef<T>): T? = (values[key] as? T) ?: key.default

    fun <T> contains(key: RuleDef<T>): Boolean = values.containsKey(key)

    fun keys(): Set<RuleDef<*>> = values.keys

    fun merge(override: FormatterStyleConfig): FormatterStyleConfig = FormatterStyleConfig(values + override.values)

}
