//import com.google.gson.Gson
//import com.google.gson.reflect.TypeToken
//import config.FormatterStyleConfig
//import config.RuleDef
//import config.RuleIdNameAdapter
//import kotlinx.serialization.json.JsonPrimitive
//
//class ConfigLoader(
//    private val adapter: RuleIdNameAdapter,
//) {
//    private val gson = Gson()
//    private val mapType = object : TypeToken<Map<String, Any?>>() {}.type
//    // para decirle a gson como a que quiero parsear el json
//
//    fun loadFromString(text: String): FormatterStyleConfig {
//        val raw: Map<String, Any?> = gson.fromJson(text, mapType) ?: emptyMap()
//
//        val values = mutableMapOf<RuleDef<*>, Any>()
//
//        for ((externalName, anyValue) in raw) {
//            val id = adapter.resolve(externalName) ?: continue // busco que nombre tiene mi regla en el json
//
//            val prim =
//                anyToJsonPrimitive(anyValue) // parseo a json primitive para poder usar las functions
//                    ?: throw IllegalArgumentException("Invalid null for '$externalName'")
//
//            @Suppress("UNCHECKED_CAST")
//            val parsed: Any = (id as RuleDef<Any>).parse(prim)
//
//            values[id] = parsed
//        }
//        return FormatterStyleConfig(values)
//    }
//
//    private fun anyToJsonPrimitive(v: Any?): JsonPrimitive? =
//        when (v) {
//            null -> null
//            is Boolean -> JsonPrimitive(v)
//            is Number -> {
//                val d = v.toDouble() // el json me los da como double
//                if (d % 1.0 != 0.0) { // si no es entero exacto rechazo
//                    throw IllegalArgumentException("Expected integer but got non-integer: $v")
//                }
//                val asLong = d.toLong() // ahora me fijo de long
//                if (asLong < Int.MIN_VALUE || asLong > Int.MAX_VALUE) {
//                    throw IllegalArgumentException("Integer out of range for Int: $v")
//                }
//                JsonPrimitive(asLong.toInt()) // paso a int
//            }
//            is String -> JsonPrimitive(v)
//            else -> throw IllegalArgumentException("Unsupported type: ${v::class.java} ($v)")
//        }
//}

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import config.FormatterStyleConfig
import config.RuleDef
import config.RuleIdNameAdapter
import kotlinx.serialization.json.JsonPrimitive

class ConfigLoader(
    private val adapter: RuleIdNameAdapter,
) {
    private val gson = Gson()
    private val mapType = object : TypeToken<Map<String, Any?>>() {}.type

    fun loadFromString(text: String): FormatterStyleConfig {
        val raw: Map<String, Any?> = gson.fromJson(text, mapType) ?: emptyMap()

        val explicit: MutableMap<RuleDef<*>, Any?> = linkedMapOf()

        for ((name, rawValue) in raw) {
            val def = adapter.resolve(name) ?: continue // o lanzá error si preferís
            // Si el JSON trae null => apagar explícitamente la regla
            val typed: Any? = if (rawValue == null) {
                null
            } else {
                val jp = toJsonPrimitiveOrThrow(rawValue)
                when (def) {
                    // si necesitás branchs por tipo, dejalo; en general:
                    else -> def.parse(jp)
                }
            }
            explicit[def] = typed
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