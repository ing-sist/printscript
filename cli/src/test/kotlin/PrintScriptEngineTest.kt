import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import progress.ConsoleProgressReporter
import java.io.File
import java.nio.file.Path

@DisplayName("PrintScriptEngine Tests")
class PrintScriptEngineTest {
    @TempDir
    lateinit var tempDir: Path

    private fun createTempFile(
        content: String,
        filename: String = "test.ps",
    ): File {
        val file = tempDir.resolve(filename).toFile()
        file.writeText(content)
        return file
    }

    @Test
    @DisplayName("Should set and validate supported version 1.0")
    fun testSetVersion10() {
        val engine = PrintScriptEngine()
        engine.setVersion("1.0")
        // If no exception is thrown, the version is supported
        assertTrue(true)
    }

    @Test
    @DisplayName("Should set and validate supported version 1.1")
    fun testSetVersion11() {
        val engine = PrintScriptEngine()
        engine.setVersion("1.1")
        // If no exception is thrown, the version is supported
        assertTrue(true)
    }

    @Test
    @DisplayName("Should throw exception for unsupported version")
    fun testSetUnsupportedVersion() {
        val engine = PrintScriptEngine()
        assertThrows(IllegalArgumentException::class.java) {
            engine.setVersion("2.0")
        }
    }

    @Test
    @DisplayName("Should validate syntax successfully")
    fun testValidateSyntax() {
        val engine = PrintScriptEngine()
        val file = createTempFile("let x: number = 10;")
        val reporter = ConsoleProgressReporter()

        engine.setVersion("1.1")
        engine.validateSyntax(file.absolutePath, reporter)
        // If no exception is thrown, validation succeeded
        assertTrue(true)
    }

    @Test
    @DisplayName("Should execute code and return output")
    fun testExecute() {
        val engine = PrintScriptEngine()
        val file = createTempFile("let x: number = 10;\nprintln(x);")
        val reporter = ConsoleProgressReporter()

        engine.setVersion("1.1")
        val output = engine.execute(file.absolutePath, reporter)
        assertTrue(output.contains("10"))
    }

    @Test
    @DisplayName("Should execute code with no output")
    fun testExecuteNoOutput() {
        val engine = PrintScriptEngine()
        val file = createTempFile("let x: number = 10;")
        val reporter = ConsoleProgressReporter()

        engine.setVersion("1.1")
        val output = engine.execute(file.absolutePath, reporter)
        assertEquals("", output)
    }

    @Test
    @DisplayName("Should analyze code without config file")
    fun testAnalyzeWithoutConfig() {
        val engine = PrintScriptEngine()
        val file = createTempFile("let myVariable: number = 10;")
        val reporter = ConsoleProgressReporter()

        engine.setVersion("1.1")
        val report = engine.analyze(file.absolutePath, reporter)
        assertNotNull(report)
    }

    @Test
    @DisplayName("Should analyze code with config file")
    fun testAnalyzeWithConfig() {
        val engine = PrintScriptEngine()
        val codeFile = createTempFile("let my_variable: number = 10;", "code.ps")
        val configFile = createTempFile("""{"identifier-naming": "camel case"}""", "config.json")
        val reporter = ConsoleProgressReporter()

        engine.setVersion("1.1")
        engine.setAnalyzerConfig(configFile.absolutePath)
        val report = engine.analyze(codeFile.absolutePath, reporter)
        assertNotNull(report)
        // The report should be processed successfully (may or may not have issues depending on config)
        assertTrue(true, "Analysis completed with config")
    }

    @Test
    @DisplayName("Should format code without style config")
    fun testFormatWithoutConfig() {
        val engine = PrintScriptEngine()
        val file = createTempFile("let x:number=10;")
        val reporter = ConsoleProgressReporter()

        engine.setVersion("1.1")
        val formatted = engine.format(file.absolutePath, reporter)
        assertNotNull(formatted)
        assertTrue(formatted.isNotEmpty())
    }

    @Test
    @DisplayName("Should format code with style config")
    fun testFormatWithConfig() {
        val engine = PrintScriptEngine()
        val codeFile = createTempFile("let x:number=10;", "code.ps")
        val styleFile =
            createTempFile(
                """
                {
                    "space-before-colon": true,
                    "space-after-colon": true
                }
                """.trimIndent(),
                "style.json",
            )
        val reporter = ConsoleProgressReporter()

        engine.setVersion("1.1")
        engine.setFormatterConfig(styleFile.absolutePath)
        val formatted = engine.format(codeFile.absolutePath, reporter)
        assertNotNull(formatted)
        assertTrue(formatted.isNotEmpty())
    }

    @Test
    @DisplayName("Should handle parse errors gracefully")
    fun testParseError() {
        val engine = PrintScriptEngine()
        val file = createTempFile("let x =;") // Invalid syntax
        val reporter = ConsoleProgressReporter()

        engine.setVersion("1.1")
        assertThrows(IllegalStateException::class.java) {
            engine.validateSyntax(file.absolutePath, reporter)
        }
    }

    @Test
    @DisplayName("Should handle execution errors gracefully")
    fun testExecutionError() {
        val engine = PrintScriptEngine()
        val file = createTempFile("let x =;") // Invalid syntax
        val reporter = ConsoleProgressReporter()

        engine.setVersion("1.1")
        assertThrows(IllegalStateException::class.java) {
            engine.execute(file.absolutePath, reporter)
        }
    }

    @Test
    @DisplayName("Should process multiple statements")
    fun testMultipleStatements() {
        val engine = PrintScriptEngine()
        val file =
            createTempFile(
                """
                let x: number = 10;
                let y: number = 20;
                let z: number = 30;
                println(x);
                println(y);
                println(z);
                """.trimIndent(),
            )
        val reporter = ConsoleProgressReporter()

        engine.setVersion("1.1")
        val output = engine.execute(file.absolutePath, reporter)
        assertTrue(output.contains("10"))
        assertTrue(output.contains("20"))
        assertTrue(output.contains("30"))
    }

    @Test
    @DisplayName("Should handle empty file")
    fun testEmptyFile() {
        val engine = PrintScriptEngine()
        val file = createTempFile("")
        val reporter = ConsoleProgressReporter()

        engine.setVersion("1.1")
        val output = engine.execute(file.absolutePath, reporter)
        assertEquals("", output)
    }

    @Test
    @DisplayName("Should set analyzer config to null")
    fun testSetAnalyzerConfigNull() {
        val engine = PrintScriptEngine()
        engine.setAnalyzerConfig(null)
        // Should not throw exception
        assertTrue(true)
    }

    @Test
    @DisplayName("Should set formatter config to null")
    fun testSetFormatterConfigNull() {
        val engine = PrintScriptEngine()
        engine.setFormatterConfig(null)
        // Should not throw exception
        assertTrue(true)
    }

    @Test
    @DisplayName("Should validate with version 1.0")
    fun testValidateWithV10() {
        val engine = PrintScriptEngine()
        val file = createTempFile("let x: number = 10;")
        val reporter = ConsoleProgressReporter()

        engine.setVersion("1.0")
        engine.validateSyntax(file.absolutePath, reporter)
        assertTrue(true)
    }

    @Test
    @DisplayName("Should execute with version 1.0")
    fun testExecuteWithV10() {
        val engine = PrintScriptEngine()
        val file = createTempFile("let x: number = 10;\nprintln(x);")
        val reporter = ConsoleProgressReporter()

        engine.setVersion("1.0")
        val output = engine.execute(file.absolutePath, reporter)
        assertTrue(output.contains("10"))
    }
}
