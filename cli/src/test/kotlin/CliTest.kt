import com.github.ajalt.clikt.testing.test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.File

class CliTest {
    private val engine = PrintScriptEngine()

    @Test
    fun validate_valid_file() {
        val cmd = ValidateCommand(engine)
        val src = resourcePath("valid.ps")
        val result = cmd.test("$src")
        assertEquals("File is valid\n", result.output)
    }

    @Test
    fun validate_invalid_file_reports_error() {
        val cmd = ValidateCommand(engine)
        val src = resourcePath("invalid.ps")
        val result = cmd.test("$src")
        assertTrue(result.output.startsWith("Error:"))
        assertTrue(result.output.contains("Parse"))
    }

    @Test
    fun validate_with_show_progress_prints_progress() {
        val cmd = ValidateCommand(engine)
        val src = resourcePath("valid.ps")
        val result = cmd.test("$src --show-progress")
        assertTrue(result.output.contains("Tokens:"))
        assertTrue(result.output.contains("Parsing..."))
        assertTrue(result.output.contains("File is valid"))
    }

    @Test
    fun execute_prints_number() {
        val cmd = ExecuteCommand(engine)
        val src = resourcePath("exec-ok.ps")
        val result = cmd.test("$src")
        assertTrue(result.output.contains("10"))
    }

    @Test
    fun execute_with_env_variable() {
        val cmd = ExecuteCommand(engine)
        val src = resourcePath("exec-env.ps")
        val result = cmd.test("$src --env FOO=bar")
        assertTrue(result.output.contains("bar"))
    }

    @Test
    fun execute_with_input_file() {
        val cmd = ExecuteCommand(engine)
        val src = resourcePath("exec-input.ps")
        val input = resourcePath("exec-input.txt")
        val result = cmd.test("$src --input $input")
        assertTrue(result.output.contains("Alice"))
    }

    @Test
    fun execute_with_show_progress_and_silent_produces_no_output() {
        val cmd = ExecuteCommand(engine)
        val src = resourcePath("exec-ok.ps")
        val result = cmd.test("$src --show-progress --silent")
        assertEquals("", result.output)
    }

    @Test
    fun execute_invalid_program_reports_error() {
        val cmd = ExecuteCommand(engine)
        val src = resourcePath("invalid.ps")
        val result = cmd.test("$src")
        assertTrue(result.output.startsWith("Error:"))
    }

    @Test
    fun analyze_no_issues_with_camel_config() {
        val cmd = AnalyzeCommand(engine)
        val src = resourcePath("analyzer-ok.ps")
        val cfg = resourcePath("analyzer-camel.json")
        val result = cmd.test("$src --rules $cfg")
        assertEquals("No issues found\n", result.output)
    }

    @Test
    fun analyze_reports_issues_with_camel_config() {
        val cmd = AnalyzeCommand(engine)
        val src = resourcePath("analyzer-bad.ps")
        val cfg = resourcePath("analyzer-camel.json")
        val result = cmd.test("$src --rules $cfg")
        assertTrue(result.output.contains("Found"))
        assertTrue(result.output.contains("issue(s)"))
    }

    private fun resourcePath(name: String): String {
        val url =
            this::class.java.classLoader.getResource(name)
                ?: error("Missing test resource: $name")
        return File(url.toURI()).absolutePath
    }
}
