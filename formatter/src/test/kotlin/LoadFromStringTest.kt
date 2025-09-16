import config.FormatterStyleConfig
import config.RuleDefinitions.RULES
import org.junit.jupiter.api.Test
import rules.definitions.SpaceAfterColonDef
import rules.definitions.SpaceBeforeColonDef
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LoadStyleMapFromStringTest {
    @Test
    fun `loadStyleMapFromString parsea json simple (solo USER)`() {
        val json =
            """
            {
              "${SpaceBeforeColonDef.id}": false,
              "${SpaceAfterColonDef.id}": true
            }
            """.trimIndent()

        val result = loadStyleMapFromString(json, RULES)

        // Solo devuelve lo provisto por el usuario
        assertEquals(setOf(SpaceBeforeColonDef.id, SpaceAfterColonDef.id), result.keys)
        assertEquals(false, result[SpaceBeforeColonDef.id])
        assertEquals(true, result[SpaceAfterColonDef.id])
    }

    @Test
    fun `loadStyleMapFromString con json vacio devuelve mapa vacio`() {
        val json = "{}"
        val result = loadStyleMapFromString(json, RULES)
        assertTrue(result.isEmpty())
    }
}

class FormatterStyleConfigOverlayTest {
    @Test
    fun `FormatterStyleConfig fromJson hace overlay con defaults`() {
        val json = """{ "${SpaceBeforeColonDef.id}": false }"""
        val style = FormatterStyleConfig.fromJson(json)

        // Cambió lo provisto
        assertFalse(style.spaceBeforeColon)
        // El resto queda en default (no importa si la regla no está activa)
        // Puedes verificar uno o dos defaults para sanity check
        // p.ej. spaceAfterColon default=true
        assertTrue(style.spaceAfterColon)
    }
}
