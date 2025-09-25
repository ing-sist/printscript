import ConfigLoader
import config.BelowLineBraceIfStatementDef
import config.IndentationDef
import config.InlineBraceIfStatementIdDef
import config.LineBreakAfterSemiColonDef
import config.LineBreakBeforePrintlnDef
import config.MaxSpaceBetweenTokensDef
import config.RuleIdNameAdapter
import config.SpaceAfterColonDef
import config.SpaceAroundAssignmentDef
import config.SpaceAroundOperatorsDef
import config.SpaceBeforeColonDef
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test


class ConfigLoaderIoTest {

    // Adapter realista: mapea los "externalName" del JSON a los RuleDef del sistema.
    // Si agregás más RuleDef en el futuro, sumalos acá.
    private val adapter = RuleIdNameAdapter { name ->
        when (name) {
            "SpaceAroundOperators" -> SpaceAroundOperatorsDef
            "SpaceBeforeColon" -> SpaceBeforeColonDef
            "SpaceAfterColon" -> SpaceAfterColonDef
            "SpaceAroundAssignment" -> SpaceAroundAssignmentDef
            "LineBreakBeforePrintln" -> LineBreakBeforePrintlnDef
            "LineBreakAfterSemiColon" -> LineBreakAfterSemiColonDef
            "MaxSpaceBetweenTokens" -> MaxSpaceBetweenTokensDef
            "Indentation" -> IndentationDef
            "InlineBraceIfStatement" -> InlineBraceIfStatementIdDef
            "BelowLineBraceIfStatement" -> BelowLineBraceIfStatementDef
            else -> null
        }
    }

    private fun loader() = ConfigLoader(adapter)

    @Test
    fun `carga valida desde string y aplica valores`() {
        val json = """
            {
              "SpaceBeforeColon": true,
              "SpaceAfterColon": false,
              "LineBreakAfterSemiColon": true,
              "Indentation": 2
            }
        """.trimIndent()

        val style = loader().loadFromString(json)

        // valores provistos
        assertEquals(true, style[SpaceBeforeColonDef])
        assertEquals(false, style[SpaceAfterColonDef])
        assertEquals(true, style[LineBreakAfterSemiColonDef])
        assertEquals(2, style[IndentationDef])

        // valores no provistos -> defaults de RuleDef.kt
        assertEquals(true, style[SpaceAroundOperatorsDef]) // default = true
        assertEquals(true, style[MaxSpaceBetweenTokensDef]) // default = true
        assertEquals(null, style[SpaceAroundAssignmentDef]) // default = null
        assertEquals(null, style[LineBreakBeforePrintlnDef]) // default = null
        assertEquals(null, style[InlineBraceIfStatementIdDef])
        assertEquals(null, style[BelowLineBraceIfStatementDef])

        // contains/keys reflejan SOLO lo provisto explícitamente
        assertTrue(style.contains(SpaceBeforeColonDef))
        assertTrue(style.contains(SpaceAfterColonDef))
        assertTrue(style.contains(LineBreakAfterSemiColonDef))
        assertTrue(style.contains(IndentationDef))
        assertFalse(style.contains(SpaceAroundOperatorsDef))
    }

    @Test
    fun `json vacio usa defaults (map vacio, getters devuelven default de cada regla)`() {
        val style = loader().loadFromString("{}")

        // algunos chequeos representativos de defaults definidos en RuleDef.kt
        assertEquals(true, style[SpaceAroundOperatorsDef])      // default = true
        assertEquals(4, style[IndentationDef])                  // default = 4
        assertEquals(true, style[LineBreakAfterSemiColonDef])   // default = true

        // muchos defaults son null -> sigue siendo válido
        assertEquals(null, style[SpaceBeforeColonDef])
        assertEquals(null, style[SpaceAfterColonDef])

        // el mapa interno no tiene overrides
        assertTrue(style.keys().isEmpty())
    }

    @Test
    fun `claves desconocidas se ignoran sin romper`() {
        val json = """
            {
              "SpaceBeforeColon": true,
              "ThisKeyDoesNotExist": "whatever",
              "Indentation": 8
            }
        """.trimIndent()

        val style = loader().loadFromString(json)

        // conocidas aplicadas
        assertEquals(true, style[SpaceBeforeColonDef])
        assertEquals(8, style[IndentationDef])
        println(style[IndentationDef])

        // la desconocida no aparece en keys ni rompe
        assertEquals(setOf(SpaceBeforeColonDef, IndentationDef), style.keys())
    }

    @Test
    fun `valor mal tipeado produce error claro (Indentation string)`() {
        val json = """
            { "Indentation": "dos" }
        """.trimIndent()

        // RuleDef.IndentationDef.parse usa intOrNull y 'error(...)' -> IllegalStateException
        assertThrows(IllegalStateException::class.java) {
            loader().loadFromString(json)
        }
    }

    @Test
    fun `valor null en json produce IllegalArgumentException del loader`() {
        val json = """
            { "SpaceBeforeColon": null }
        """.trimIndent()

        // ConfigLoader.anyToJsonPrimitive devuelve null -> "Invalid null for 'SpaceBeforeColon'"
        val ex = assertThrows(IllegalArgumentException::class.java) {
            loader().loadFromString(json)
        }
        assertTrue(ex.message?.contains("Invalid null for 'SpaceBeforeColon'") == true)
    }

    @Test
    fun `merge combina mapas y override pisa al base`() {
        val baseJson = """{ "SpaceBeforeColon": false, "Indentation": 4 }""".trimIndent()
        val overrideJson = """{ "SpaceBeforeColon": true, "SpaceAfterColon": true }""".trimIndent()

        val base = loader().loadFromString(baseJson)
        val override = loader().loadFromString(overrideJson)

        val merged = base.merge(override)

        // override pisa
        assertEquals(true, merged[SpaceBeforeColonDef])

        // conserva lo no pisado del base
        assertEquals(4, merged[IndentationDef])

        // suma nuevas del override
        assertEquals(true, merged[SpaceAfterColonDef])

        // keys contiene exactamente las definidas por cualquiera de los dos
        assertEquals(
            setOf(SpaceBeforeColonDef, IndentationDef, SpaceAfterColonDef),
            merged.keys()
        )
    }
}