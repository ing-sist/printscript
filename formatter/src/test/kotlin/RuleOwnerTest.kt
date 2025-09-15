import config.FormatterStyleConfig
import config.RuleDefinitions
import config.RuleOwner
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import rules.definitions.InlineIfBraceIfStatementDef
import rules.definitions.LineBreakAfterSemiColonDef
import rules.definitions.SpaceAroundOperatorsDef

class RuleOwnerTest {
    private val rules = RuleDefinitions.RULES

    private fun configFrom(json: String): FormatterStyleConfig {
        val tmp =
            kotlin.io.path
                .createTempFile("fmt-", ".json")
                .toFile()
        tmp.writeText(json.trimIndent())
        val map = loadFromFile(tmp, rules)
        return FormatterStyleConfig.fromMap(map)
    }

    @Test
    fun `USER overrides se aplican y ENGINE ignora archivo`() {
        val style =
            configFrom(
                """
                {
                  "lineBreakBeforePrintln": 2,        
                  "lineBreakAfterSemicolon": false,  
                  "spaceAroundAssignment": false,     
                  "spaceAroundOperators": false,      
                  "indentation": 6,                  
                  "inlineIfBraceIfStatement": false   
                }
                """.trimIndent(),
            )

//        "lineBreakBeforePrintln": 2,        // USER: override OK (default=1)
//        "lineBreakAfterSemicolon": false,   // ENGINE: debe ignorar (default=true)
//        "spaceAroundAssignment": false,     // USER: override OK (default=true)
//        "spaceAroundOperators": false,      // ENGINE: debe ignorar (default=true)
//        "indentation": 6,                   // USER: override OK (default=4)
//        "inlineIfBraceIfStatement": false   // ENGINE: debe ignorar (default=true)
//

        assertEquals(2, style.lineBreakBeforePrintln)
        assertFalse(style.spaceAroundAssignment)
        assertEquals(6, style.indentation)

        assertTrue(LineBreakAfterSemiColonDef.owner == RuleOwner.ENGINE)
        assertTrue(style.lineBreakAfterSemicolon) // default=true
        assertTrue(SpaceAroundOperatorsDef.owner == RuleOwner.ENGINE)
        assertTrue(style.spaceAroundOperators) // default=true
        assertTrue(InlineIfBraceIfStatementDef.owner == RuleOwner.ENGINE)
        assertTrue(style.inlineIfBraceIfStatement) // default=true
    }

    @Test
    fun `faltantes usan default de USER`() {
        val style =
            configFrom(
                """
            {
              "lineBreakBeforePrintln": 1
            }
            """,
            )

        assertEquals(1, style.lineBreakBeforePrintln)

        assertTrue(style.spaceBeforeColon) // default=true
        assertTrue(style.spaceAfterColon) // default=true
        assertTrue(style.spaceAroundAssignment) // default=true
        assertEquals(4, style.indentation) // default=4
    }

    @Test
    fun `ENGINE siempre default aunque venga en JSON`() {
        val style =
            configFrom(
                """
            {
              "lineBreakAfterSemicolon": false,
              "spaceAroundOperators": false,
              "inlineIfBraceIfStatement": false
            }
            """,
            )
        assertTrue(style.lineBreakAfterSemicolon)
        assertTrue(style.spaceAroundOperators)
        assertTrue(style.inlineIfBraceIfStatement)
    }

    @Test
    fun `claves desconocidas no rompen y se ignoran`() {
        val style =
            configFrom(
                """
            {
              "indentation": 2,
              "unknownThing": 123,
              "spaceBeforeColon": false
            }
            """,
            )
        assertEquals(2, style.indentation)
        assertFalse(style.spaceBeforeColon)

        assertTrue(style.spaceAfterColon)
        assertTrue(style.spaceAroundAssignment)
    }
}
