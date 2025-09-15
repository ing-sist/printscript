import config.FormatterRuleImplementations
import config.RuleDefinitions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import rules.implementations.RuleImplementation

class FormatterRuleImplementationsTest {
    @Test
    fun `getRuleImplementations retorna lista de reglas`() {
        val rules = FormatterRuleImplementations.IMPLEMENTATIONS

        assertNotNull(rules)
        assertTrue(rules.isNotEmpty())
        assertTrue(rules.all { rule -> rule is RuleImplementation })
    }

    @Test
    fun `getRuleImplementations incluye reglas basicas`() {
        val rules = FormatterRuleImplementations.IMPLEMENTATIONS
        val ruleNames = rules.map { rule -> rule::class.simpleName }

        // Verificar que se incluyen las reglas principales
        assertTrue(ruleNames.contains("SpaceAroundAssignment"))
        assertTrue(ruleNames.contains("LineBreakAfterSemicolon"))
        assertTrue(ruleNames.contains("ColonSpacing"))
        assertTrue(ruleNames.contains("Indentation"))
    }
}

class LoaderTest {
    private fun createTempConfigFile(content: String): java.io.File {
        val tmp =
            kotlin.io.path
                .createTempFile("config-", ".json")
                .toFile()
        tmp.writeText(content.trimIndent())
        return tmp
    }

    @Test
    fun `loadFromFile carga configuracion desde archivo JSON`() {
        val configFile =
            createTempConfigFile(
                """
            {
              "lineBreakBeforePrintln": 2,
              "spaceAroundAssignment": false,
              "indentation": 8
            }
        """,
            )

        val result = config.loadFromFile(configFile, RuleDefinitions.RULES)

        assertEquals(2, result["lineBreakBeforePrintln"])
        assertEquals(false, result["spaceAroundAssignment"])
        assertEquals(8, result["indentation"])

        configFile.delete()
    }

    @Test
    fun `loadFromFile aplica valores por defecto para claves faltantes`() {
        val configFile =
            createTempConfigFile(
                """
            {
              "indentation": 6
            }
        """,
            )

        val result = config.loadFromFile(configFile, RuleDefinitions.RULES)

        assertEquals(6, result["indentation"])
        // Los valores por defecto deben estar presentes
        assertTrue(result.containsKey("lineBreakBeforePrintln"))
        assertTrue(result.containsKey("spaceAroundAssignment"))

        configFile.delete()
    }

    @Test
    fun `loadFromFile maneja archivo JSON vacio`() {
        val configFile = createTempConfigFile("{}")

        val result = config.loadFromFile(configFile, RuleDefinitions.RULES)

        // Todos los valores por defecto deben estar presentes
        assertTrue(result.containsKey("lineBreakBeforePrintln"))
        assertTrue(result.containsKey("lineBreakAfterSemicolon"))
        assertTrue(result.containsKey("spaceBeforeColon"))
        assertTrue(result.containsKey("spaceAfterColon"))
        assertTrue(result.containsKey("spaceAroundAssignment"))
        assertTrue(result.containsKey("spaceAroundOperators"))
        assertTrue(result.containsKey("indentation"))
        assertTrue(result.containsKey("inlineIfBraceIfStatement"))

        configFile.delete()
    }
}
