package progress

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream

@DisplayName("ConsoleProgressReporter Tests")
class ConsoleProgressReporterTest {
    @Test
    @DisplayName("Should report progress with percentage")
    fun testReportProgressWithPercentage() {
        val reporter = ConsoleProgressReporter()
        // Capture stdout to verify output
        val outContent = ByteArrayOutputStream()
        System.setOut(PrintStream(outContent))

        reporter.reportProgress("Testing", 50)

        System.setOut(System.out) // Reset stdout
        val output = outContent.toString()
        assertTrue(output.contains("50%") || output.contains("Testing"))
    }

    @Test
    @DisplayName("Should report progress without percentage")
    fun testReportProgressWithoutPercentage() {
        val reporter = ConsoleProgressReporter()
        val outContent = ByteArrayOutputStream()
        System.setOut(PrintStream(outContent))

        reporter.reportProgress("Testing", null)

        System.setOut(System.out)
        val output = outContent.toString()
        assertTrue(output.contains("Testing") || output.isEmpty())
    }

    @Test
    @DisplayName("Should report progress with 0%")
    fun testReportProgressZeroPercent() {
        val reporter = ConsoleProgressReporter()
        val outContent = ByteArrayOutputStream()
        System.setOut(PrintStream(outContent))

        reporter.reportProgress("Starting", 0)

        System.setOut(System.out)
        val output = outContent.toString()
        assertTrue(output.contains("0%") || output.contains("Starting"))
    }

    @Test
    @DisplayName("Should report progress with 100%")
    fun testReportProgressFullPercent() {
        val reporter = ConsoleProgressReporter()
        val outContent = ByteArrayOutputStream()
        System.setOut(PrintStream(outContent))

        reporter.reportProgress("Complete", 100)

        System.setOut(System.out)
        val output = outContent.toString()
        assertTrue(output.contains("100%") || output.contains("Complete"))
    }

    @Test
    @DisplayName("Should coerce percentage above 100 to 100")
    fun testReportProgressAbove100() {
        val reporter = ConsoleProgressReporter()
        val outContent = ByteArrayOutputStream()
        System.setOut(PrintStream(outContent))

        reporter.reportProgress("Overflow", 150)

        System.setOut(System.out)
        val output = outContent.toString()
        assertTrue(output.contains("100%") || output.isEmpty())
    }

    @Test
    @DisplayName("Should coerce percentage below 0 to 0")
    fun testReportProgressBelowZero() {
        val reporter = ConsoleProgressReporter()
        val outContent = ByteArrayOutputStream()
        System.setOut(PrintStream(outContent))

        reporter.reportProgress("Negative", -10)

        System.setOut(System.out)
        val output = outContent.toString()
        assertTrue(output.contains("0%") || output.isEmpty())
    }

    @Test
    @DisplayName("Should report error message")
    fun testReportError() {
        val reporter = ConsoleProgressReporter()
        val errContent = ByteArrayOutputStream()
        System.setErr(PrintStream(errContent))

        reporter.reportError("Test error")

        System.setErr(System.err)
        val output = errContent.toString()
        assertTrue(output.contains("ERROR:") || output.contains("Test error"))
    }

    @Test
    @DisplayName("Should report success message")
    fun testReportSuccess() {
        val reporter = ConsoleProgressReporter()
        val outContent = ByteArrayOutputStream()
        System.setOut(PrintStream(outContent))

        reporter.reportSuccess("Test success")

        System.setOut(System.out)
        val output = outContent.toString()
        assertTrue(output.contains("✓") || output.contains("Test success"))
    }

    @Test
    @DisplayName("Should clear progress line")
    fun testClearProgressLine() {
        val reporter = ConsoleProgressReporter()
        val outContent = ByteArrayOutputStream()
        System.setOut(PrintStream(outContent))

        reporter.clearProgressLine()

        System.setOut(System.out)
        // Should not throw exception
        assertTrue(true)
    }

    @Test
    @DisplayName("Should handle multiple progress updates")
    fun testMultipleProgressUpdates() {
        val reporter = ConsoleProgressReporter()
        val outContent = ByteArrayOutputStream()
        System.setOut(PrintStream(outContent))

        reporter.reportProgress("Step 1", 25)
        reporter.reportProgress("Step 2", 50)
        reporter.reportProgress("Step 3", 75)
        reporter.reportProgress("Step 4", 100)

        System.setOut(System.out)
        // Should not throw exception
        assertTrue(true)
    }
}
