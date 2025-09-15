import config.FormatterStyleConfig
import config.RuleDefinitions
import config.loadFromFile
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.File

class FormatterStyleConfigTest {
    private val rules = RuleDefinitions.RULES

    private fun createTempConfigFile(content: String): File {
        val tmp =
            kotlin.io.path
                .createTempFile("config-", ".json")
                .toFile()
        tmp.writeText(content.trimIndent())
        return tmp
    }

    @Test
    fun `fromMap crea configuracion con valores especificos`() {
        val map =
            mapOf(
                "lineBreakBeforePrintln" to 3,
                "lineBreakAfterSemicolon" to false,
                "spaceBeforeColon" to false,
                "spaceAfterColon" to false,
                "spaceAroundAssignment" to false,
                "spaceAroundOperators" to false,
                "indentation" to 8,
                "inlineIfBraceIfStatement" to false,
            )

        val config = FormatterStyleConfig.fromMap(map)

        assertEquals(3, config.lineBreakBeforePrintln)
        assertFalse(config.lineBreakAfterSemicolon)
        assertFalse(config.spaceBeforeColon)
        assertFalse(config.spaceAfterColon)
        assertFalse(config.spaceAroundAssignment)
        assertFalse(config.spaceAroundOperators)
        assertEquals(8, config.indentation)
        assertFalse(config.inlineIfBraceIfStatement)
    }

    @Test
    fun `fromPath carga configuracion desde archivo JSON valido`() {
        val configFile =
            createTempConfigFile(
                """
            {
              "lineBreakBeforePrintln": 2,
              "spaceAroundAssignment": false,
              "indentation": 6
            }
        """,
            )

        val config = FormatterStyleConfig.fromPath(configFile.absolutePath)

        assertEquals(2, config.lineBreakBeforePrintln)
        assertFalse(config.spaceAroundAssignment)
        assertEquals(6, config.indentation)

        configFile.delete()
    }

    @Test
    fun `configuracion con todos los valores por defecto`() {
        val configFile =
            createTempConfigFile(
                """
            {
              "lineBreakBeforePrintln": 1,
              "lineBreakAfterSemicolon": true,
              "spaceBeforeColon": true,
              "spaceAfterColon": true,
              "spaceAroundAssignment": true,
              "spaceAroundOperators": true,
              "indentation": 4,
              "inlineIfBraceIfStatement": true
            }
        """,
            )

        val config = FormatterStyleConfig.fromPath(configFile.absolutePath)

        assertEquals(1, config.lineBreakBeforePrintln)
        assertTrue(config.lineBreakAfterSemicolon)
        assertTrue(config.spaceBeforeColon)
        assertTrue(config.spaceAfterColon)
        assertTrue(config.spaceAroundAssignment)
        assertTrue(config.spaceAroundOperators)
        assertEquals(4, config.indentation)
        assertTrue(config.inlineIfBraceIfStatement)

        configFile.delete()
    }

    @Test
    fun `configuracion JSON vacio usa defaults`() {
        val configFile = createTempConfigFile("{}")
        val map = loadFromFile(configFile, rules)
        val config = FormatterStyleConfig.fromMap(map)

        // Verifica que se usen los valores por defecto cuando el JSON está vacío
        assertEquals(1, config.lineBreakBeforePrintln) // default
        assertTrue(config.lineBreakAfterSemicolon) // default
        assertTrue(config.spaceBeforeColon) // default
        assertTrue(config.spaceAfterColon) // default
        assertTrue(config.spaceAroundAssignment) // default
        assertTrue(config.spaceAroundOperators) // default
        assertEquals(4, config.indentation) // default
        assertTrue(config.inlineIfBraceIfStatement) // default

        configFile.delete()
    }

    @Test
    fun `configuracion con claves desconocidas las ignora`() {
        val configFile =
            createTempConfigFile(
                """
            {
              "indentation": 2,
              "unknownProperty": "ignored",
              "anotherUnknown": 999,
              "spaceBeforeColon": false
            }
        """,
            )

        val config = FormatterStyleConfig.fromPath(configFile.absolutePath)

        assertEquals(2, config.indentation)
        assertFalse(config.spaceBeforeColon)
        // Las propiedades desconocidas se ignoran sin error

        configFile.delete()
    }

    @Test
    fun `configuracion parcial mezcla valores especificos con defaults`() {
        val configFile =
            createTempConfigFile(
                """
            {
              "lineBreakBeforePrintln": 5,
              "indentation": 8,
              "spaceAroundAssignment": false
            }
        """,
            )

        val config = FormatterStyleConfig.fromPath(configFile.absolutePath)

        // Valores especificados
        assertEquals(5, config.lineBreakBeforePrintln)
        assertEquals(8, config.indentation)
        assertFalse(config.spaceAroundAssignment)

        // Valores por defecto para los no especificados
        assertTrue(config.lineBreakAfterSemicolon)
        assertTrue(config.spaceBeforeColon)
        assertTrue(config.spaceAfterColon)
        assertTrue(config.spaceAroundOperators)
        assertTrue(config.inlineIfBraceIfStatement)

        configFile.delete()
    }
}
