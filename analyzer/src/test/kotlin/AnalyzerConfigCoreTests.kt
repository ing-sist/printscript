// src/test/kotlin/analyzer/AnalyzerConfigCoreTests.kt
import naming.IdentifierNamingRuleDef
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import shared.AnalyzerRuleDefinitions

class AnalyzerConfigCoreTests {
    private fun cfgFrom(json: String): AnalyzerConfig {
        val tmp =
            kotlin.io.path
                .createTempFile("analyzer-", ".json")
                .toFile()
        tmp.writeText(json.trimIndent())
        return AnalyzerConfig.fromPath(tmp.path, AnalyzerRuleDefinitions.RULES)
    }

    @Test
    @DisplayName("get(def) devuelve la config cuando existe")
    fun get_returnsConfig_whenPresent() {
        // Habilitamos la regla de naming con un estilo válido
        val cfg =
            cfgFrom(
                """
                {
                  "identifierNamingStyle": "camel"
                }
                """,
            )

        // No nos interesa la forma exacta del objeto, solo que exista y se tipée
        val namingCfg = cfg.get(IdentifierNamingRuleDef)
        assertNotNull(namingCfg)
    }

    @Test
    @DisplayName("get(def) lanza IllegalStateException cuando falta la config")
    fun get_throws_whenMissing() {
        val cfg = cfgFrom("{}")

        val ex =
            assertThrows(IllegalStateException::class.java) {
                cfg.get(IdentifierNamingRuleDef)
            }
        // El mensaje debe incluir el id de la regla
        assertTrue(ex.message!!.contains("Missing config for rule '${IdentifierNamingRuleDef.id}'"))
    }

    @Test
    @DisplayName("tryGet(def) devuelve la config cuando existe")
    fun tryGet_returnsConfig_whenPresent() {
        val cfg =
            cfgFrom(
                """
                {
                  "identifierNamingStyle": "snake"
                }
                """,
            )

        val namingCfg = cfg.tryGet(IdentifierNamingRuleDef)
        assertNotNull(namingCfg)
    }

    @Test
    @DisplayName("tryGet(def) devuelve null cuando falta la config")
    fun tryGet_returnsNull_whenMissing() {
        val cfg = cfgFrom("{}")

        val namingCfg = cfg.tryGet(IdentifierNamingRuleDef)
        assertNull(namingCfg)
    }
}
