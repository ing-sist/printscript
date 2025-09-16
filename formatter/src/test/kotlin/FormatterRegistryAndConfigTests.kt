import config.FormatterStyleConfig
import config.RULE_DEFS_BY_ID
import config.RULE_TO_IMPL
import config.RuleOwner
import config.activeImplementationsFromJson
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import rules.definitions.CommaSpacingDef
import rules.definitions.IfBraceBelowLineDef
import rules.definitions.IndentationDef
import rules.definitions.InlineIfBraceIfStatementDef
import rules.definitions.KeywordDef
import rules.definitions.LineBreakAfterSemiColonDef
import rules.definitions.LineBreakBeforePrintlnDef
import rules.definitions.SpaceAfterColonDef
import rules.definitions.SpaceAroundAssignmentDef
import rules.definitions.SpaceAroundOperatorsDef
import rules.definitions.SpaceBeforeColonDef

class FormatterRegistryAndConfigTests {
    companion object {
        @JvmStatic
        @BeforeAll
        fun ensureAutoregistration() {
            // Forzamos carga de los objects para que corra el init { registerDef/Impl(...) }
            // (Tocar el .id garantiza que la clase se inicialice)
            val touch =
                listOf(
                    SpaceBeforeColonDef.id,
                    SpaceAfterColonDef.id,
                    SpaceAroundAssignmentDef.id,
                    SpaceAroundOperatorsDef.id,
                    LineBreakAfterSemiColonDef.id,
                    LineBreakBeforePrintlnDef.id,
                    InlineIfBraceIfStatementDef.id,
                    IndentationDef.id,
                    IfBraceBelowLineDef.id,
                    CommaSpacingDef.id,
                    KeywordDef.id,
                )
            assertTrue(touch.isNotEmpty())
            // sanity: el registry debe tener defs e impls
            assertTrue(RULE_DEFS_BY_ID.isNotEmpty(), "Registry de defs vacío")
            assertTrue(RULE_TO_IMPL.isNotEmpty(), "Registry de impls vacío")
        }
    }

    @Test
    fun `empty json ⇒ solo ENGINE activos`() {
        val json = "{}"

        val impls = activeImplementationsFromJson(json)
        assertTrue(impls.isNotEmpty(), "Debería al menos activar ENGINE")

        // Los ENGINE deben estar siempre
        val engineIds =
            RULE_DEFS_BY_ID.values
                .filter { it.owner == RuleOwner.ENGINE }
                .map { it.id }
                .toSet()
        val implIds = RULE_TO_IMPL.filterValues { it in impls }.keys

        assertTrue(engineIds.all { it in implIds }, "Falta alguna regla ENGINE en las implementaciones activas")

        // Elegimos una regla USER (si existe) y corroboramos que NO se active sin estar en el JSON
        val anyUserDef = RULE_DEFS_BY_ID.values.firstOrNull { it.owner == RuleOwner.USER }
        if (anyUserDef != null) {
            assertFalse(implIds.contains(anyUserDef.id), "No debería activarse una regla USER sin estar en el JSON")
        }
    }

    @Test
    fun `json con id de regla USER ⇒ activa su implementación`() {
        // Elegimos SpaceBeforeColon como ejemplo USER (ajustar si fuera ENGINE)
        val def = SpaceBeforeColonDef
        assertEquals(RuleOwner.USER, def.owner, "Este test asume que SpaceBeforeColon es USER")

        val json = """{ "${def.id}": true }"""
        val impls = activeImplementationsFromJson(json)
        val implIds = RULE_TO_IMPL.filterValues { it in impls }.keys

        assertTrue(implIds.contains(def.id), "Debe activarse la implementación para ${def.id}")
        // Y además siguen los ENGINE
        val engineIds =
            RULE_DEFS_BY_ID.values
                .filter { it.owner == RuleOwner.ENGINE }
                .map { it.id }
                .toSet()
        assertTrue(engineIds.all { it in implIds }, "Falta alguna regla ENGINE en las implementaciones activas")
    }

    @Test
    fun `loadStyleMapFromString + fromMap mapean valores correctamente`() {
        val json =
            """
            {
              "${SpaceBeforeColonDef.id}": true,
              "${SpaceAfterColonDef.id}": true,
              "${LineBreakBeforePrintlnDef.id}": 2,
              "${InlineIfBraceIfStatementDef.id}": true,
              "${IndentationDef.id}": 2
            }
            """.trimIndent()

        val styleMap = loadStyleMapFromString(json) // usa allRuleDefs() del registry
        val style = FormatterStyleConfig.fromMap(styleMap)

        assertTrue(style.spaceBeforeColon)
        assertTrue(style.spaceAfterColon)
        assertEquals(2, style.lineBreakBeforePrintln)
        assertTrue(style.inlineIfBraceIfStatement)
        assertEquals(2, style.indentation)
    }

    @Test
    fun `aliases de AliasesMap funcionan (indentation-size → IndentationDef)`() {
        // según tu AliasesMap: "indentation-size" alias de IndentationDef.id
        val json = """{ "indentation-size": 3 }"""
        val styleMap = loadStyleMapFromString(json)
        val style = FormatterStyleConfig.fromMap(styleMap)

        assertEquals(3, style.indentation, "El alias 'indentation-size' debe aplicarse a Indentation")
    }

    @Test
    fun `fromJson usa loader basado en registry (sin RuleDefinitions_Rules)`() {
        val json =
            """
            {
              "${SpaceAroundAssignmentDef.id}": true,
              "${SpaceAroundOperatorsDef.id}": true,
              "${IfBraceBelowLineDef.id}": true
            }
            """.trimIndent()

        // Si ya migraste FormatterStyleConfig.fromJson a loader sin RULES, esto debe funcionar
        val style = FormatterStyleConfig.fromJson(json)
        assertTrue(style.spaceAroundAssignment)
        assertTrue(style.spaceAroundOperators)
        assertTrue(style.ifBraceBelowLine)
    }

    @Test
    fun `activeImplementationsFromJson considera aliases también`() {
        // Aliases en tu AliasesMap:
        // "mandatory-line-break-after-statement" -> LineBreakAfterSemiColonDef.id
        val aliasJson = """{ "mandatory-line-break-after-statement": true }"""
        val impls = activeImplementationsFromJson(aliasJson)
        val ids = RULE_TO_IMPL.filterValues { it in impls }.keys
        assertTrue(ids.contains(LineBreakAfterSemiColonDef.id))
    }
}
