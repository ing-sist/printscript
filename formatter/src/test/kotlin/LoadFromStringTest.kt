import config.RuleDefinitions.RULES
import config.loadFromString
import org.junit.jupiter.api.Test
import rules.definitions.SpaceAfterColonDef
import rules.definitions.SpaceBeforeColonDef
import kotlin.test.assertEquals

class LoadFromStringTest {
    @Test
    fun `loadFromString parses simple json`() {
        val json =
            """
            {
              "${SpaceBeforeColonDef.id}": false,
              "${SpaceAfterColonDef.id}": true
            }
            """.trimIndent()

        val result = loadFromString(json, RULES)

        assertEquals(false, result[SpaceBeforeColonDef.id])
        assertEquals(true, result[SpaceAfterColonDef.id])
    }

    @Test
    fun `loadFromString with empty json returns defaults`() {
        val json = "{}"
        val result = loadFromString(json, RULES)

        // Todos los ids de las RULES deben estar presentes con sus defaults
        for (rule in RULES) {
            assertEquals(rule.default, result[rule.id])
        }
    }
}
