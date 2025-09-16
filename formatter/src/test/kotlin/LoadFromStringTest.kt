import config.RuleDefinitions.RULES
import org.junit.jupiter.api.Test
import rules.definitions.SpaceAfterColonDef
import rules.definitions.SpaceBeforeColonDef
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LoadFromStringTest {
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
