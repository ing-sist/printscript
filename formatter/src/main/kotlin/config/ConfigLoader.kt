import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import config.FormatterStyleConfig
import config.RuleDef
import config.RuleIdNameAdapter
import config.RuleMapping
import kotlinx.serialization.json.JsonPrimitive

class ConfigLoader(
    private val adapter: RuleIdNameAdapter,
) {
    private val gson = Gson()
    private val mapType = object : TypeToken<Map<String, Any?>>() {}.type

    fun loadFromString(text: String): FormatterStyleConfig {
        val raw: Map<String, Any?> = gson.fromJson(text, mapType) ?: emptyMap()

        // Guarda por RuleDef, preservando null explícito (apaga la regla)
        val explicit: MutableMap<RuleDef<*>, Any?> = linkedMapOf()

        for ((externalName, rawValue) in raw) {
            val resolved: RuleMapping = adapter.resolve(externalName) ?: continue
            val def = resolved.def

            // Parseo por tipo de la RuleDef (si es null, queda null)
            val parsed: Any? = if (rawValue == null) {
                null
            } else {
                def.parse(toJsonPrimitiveOrThrow(rawValue))
            }

            // Aplico la transformación (identidad o invertir boolean, etc.)
            val finalValue = resolved.transform(parsed)

            // Política simple ante colisión de alias sobre la misma def: el último gana.
            // (Si preferís, podés detectar y lanzar error si ya existía con otro valor.)
            explicit[def] = finalValue
        }

        return FormatterStyleConfig(explicit)
    }

    private fun toJsonPrimitiveOrThrow(v: Any?): JsonPrimitive =
        when (v) {
            null -> throw IllegalArgumentException("null should be handled before")
            is Boolean -> JsonPrimitive(v)
            is Number -> {
                val d = v.toDouble()
                if (d % 1.0 != 0.0) throw IllegalArgumentException("Expected integer but got non-integer: $v")
                val asLong = d.toLong()
                if (asLong < Int.MIN_VALUE || asLong > Int.MAX_VALUE)
                    throw IllegalArgumentException("Integer out of range for Int: $v")
                JsonPrimitive(asLong.toInt())
            }
            is String -> JsonPrimitive(v)
            else -> throw IllegalArgumentException("Unsupported type: ${v::class.java} ($v)")
        }
}