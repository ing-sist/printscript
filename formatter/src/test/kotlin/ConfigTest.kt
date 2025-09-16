import config.RULE_TO_IMPL
import config.RuleDefinitions
import config.activeImplementationsFromJson
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import rules.definitions.LineBreakAfterSemiColonDef
import rules.definitions.SpaceAfterColonDef
import rules.definitions.SpaceAroundAssignmentDef
import rules.definitions.SpaceBeforeColonDef
import java.io.File
import kotlin.io.path.createTempFile

class RuleSelectorTest {
    @Test
    fun `json vacio - activa solo ENGINE`() {
        val impls = activeImplementationsFromJson("{}", RuleDefinitions.RULES)
        assertTrue(impls.isNotEmpty())
        // chequeo indirecto: ningún id USER obligatorio aparece si no vino en JSON
        val activeIds = impls.map { it::class.simpleName }.toSet()
        // ej., si SpaceAroundAssignment es USER en tu modelo:
        assertFalse(activeIds.contains("SpaceAroundAssignment"))
    }

    @Test
    fun `RULE_TO_IMPL contiene mapeos basicos`() {
        // Sanity check del registry
        assertNotNull(RULE_TO_IMPL[SpaceAroundAssignmentDef.id])
        assertNotNull(RULE_TO_IMPL[LineBreakAfterSemiColonDef.id])
        assertNotNull(RULE_TO_IMPL[SpaceBeforeColonDef.id])
        assertNotNull(RULE_TO_IMPL[SpaceAfterColonDef.id])
    }
}

class LoaderTest {
    private fun createTempConfigFile(content: String): File {
        val f = createTempFile("config-", ".json").toFile()
        f.writeText(content.trimIndent())
        return f
    }

    @Test
    fun `loadStyleMapFromFile - carga claves USER provistas`() {
        val cfg =
            createTempConfigFile(
                """
            {
              "lineBreakBeforePrintln": 2,
              "spaceAroundAssignment": false,
              "indentation": 8
            }
            """,
            )
        val map = loadStyleMapFromFile(cfg, RuleDefinitions.RULES)

        // Solo las 3 claves provistas
        assertEquals(setOf("lineBreakBeforePrintln", "spaceAroundAssignment", "indentation"), map.keys)
        assertEquals(2, map["lineBreakBeforePrintln"])
        assertEquals(false, map["spaceAroundAssignment"])
        assertEquals(8, map["indentation"])

        cfg.delete()
    }

    @Test
    fun `loadStyleMapFromFile - JSON parcial no agrega defaults`() {
        val cfg = createTempConfigFile("""{ "indentation": 6 }""")
        val map = loadStyleMapFromFile(cfg, RuleDefinitions.RULES)

        assertEquals(setOf("indentation"), map.keys)
        assertEquals(6, map["indentation"])
        // No hay otras claves
        assertFalse(map.containsKey("spaceAroundAssignment"))
        assertFalse(map.containsKey("lineBreakBeforePrintln"))

        cfg.delete()
    }

    @Test
    fun `loadStyleMapFromFile - JSON vacio devuelve mapa vacio`() {
        val cfg = createTempConfigFile("{}")
        val map = loadStyleMapFromFile(cfg, RuleDefinitions.RULES)

        assertTrue(map.isEmpty()) // sin defaults acá
        cfg.delete()
    }
}
