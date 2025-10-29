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

        val explicit: MutableMap<RuleDef<*>, Any?> = linkedMapOf()

        for ((externalName, rawValue) in raw) {
            val resolved: RuleMapping = adapter.resolve(externalName) ?: continue
            val def = resolved.def

            val parsed: Any? =
                if (rawValue == null) {
                    null
                } else {
                    def.parse(toJsonPrimitiveOrThrow(rawValue))
                }

            val finalValue = resolved.transform(parsed)

            explicit[def] = finalValue
        }

        return FormatterStyleConfig(explicit)
    }

    private fun toJsonPrimitiveOrThrow(v: Any?): JsonPrimitive =
        when (v) {
            null -> error("null should be handled before")
            is Boolean -> JsonPrimitive(v)
            is Number -> {
                val d = v.toDouble()
                require(d % 1.0 == 0.0) { "Expected integer but got non-integer: $v" }
                val asLong = d.toLong()

                require(asLong > Int.MIN_VALUE || asLong < Int.MAX_VALUE) {
                    "Integer out of range for Int: $v"
                }
                JsonPrimitive(asLong.toInt())
            }
            is String -> JsonPrimitive(v)
            else -> error("Unsupported type: ${v::class.java} ($v)")
        }
}
