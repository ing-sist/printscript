import config.FormatterStyleConfig
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.File

class FormatterStyleConfigTest {
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
                "lineBreakBeforePrintln" to 1,
                "lineBreakAfterSemicolon" to true,
                "spaceBeforeColon" to false,
                "spaceAfterColon" to false,
                "spaceAroundAssignment" to false,
                "spaceAroundOperators" to false,
                "indentation" to 8,
                "inlineIfBraceIfStatement" to false,
                "ifBraceBelowLine" to false,
            )

        val config = FormatterStyleConfig.fromMap(map)

        assertEquals(1, config.lineBreakBeforePrintln)
        assertTrue(config.lineBreakAfterSemicolon)
        assertFalse(config.spaceBeforeColon)
        assertFalse(config.spaceAfterColon)
        assertFalse(config.spaceAroundAssignment)
        assertFalse(config.spaceAroundOperators)
        assertEquals(8, config.indentation)
        assertFalse(config.inlineIfBraceIfStatement)
        assertFalse(config.ifBraceBelowLine)
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
}
