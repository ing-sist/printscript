package ps.runtime.providers

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream

class ProvidersTest {
    @Test
    fun `BufferedOutputSink stores and retrieves output correctly`() {
        val outputSink = BufferedOutputSink()

        outputSink.print("Hello")
        outputSink.print(" ")
        outputSink.print("World")

        val output = outputSink.getOutput()
        assertEquals(listOf("Hello", " ", "World"), output)
    }

    @Test
    fun `BufferedOutputSink stores output that can be retrieved`() {
        val outputSink = BufferedOutputSink()

        outputSink.print("test")
        assertEquals(1, outputSink.getOutput().size)
        assertEquals("test", outputSink.getOutput()[0])
    }

    @Test
    fun `ProgrammaticInputProvider returns inputs in order`() {
        val inputs = mutableListOf("first", "second", "third")
        val inputProvider = ProgrammaticInputProvider(inputs)

        assertEquals("first", inputProvider.readInput("Prompt 1: "))
        assertEquals("second", inputProvider.readInput("Prompt 2: "))
        assertEquals("third", inputProvider.readInput("Prompt 3: "))
    }

    @Test
    fun `ProgrammaticInputProvider throws error when no more inputs`() {
        val inputProvider = ProgrammaticInputProvider(mutableListOf())

        try {
            inputProvider.readInput("Prompt: ")
            assertTrue(false, "Should have thrown an error")
        } catch (e: Exception) {
            assertEquals("No more inputs available", e.message)
        }
    }

    @Test
    fun `MapEnvProvider returns correct environment values`() {
        val envMap =
            mapOf(
                "TEST_VAR" to "test_value",
                "NUMBER" to "42",
                "EMPTY" to "",
            )
        val envProvider = MapEnvProvider(envMap)

        assertEquals("test_value", envProvider.getEnvVariable("TEST_VAR"))
        assertEquals("42", envProvider.getEnvVariable("NUMBER"))
        assertEquals("", envProvider.getEnvVariable("EMPTY"))
        assertNull(envProvider.getEnvVariable("NON_EXISTENT"))
    }

    @Test
    fun `SystemEnvProvider can access system environment variables`() {
        val envProvider = SystemEnvProvider()

        // Test with PATH which should exist on most systems
        val path = envProvider.getEnvVariable("PATH")
        // PATH might be null in some test environments, but the method should not throw
        assertTrue(path == null || path.isNotEmpty())
    }

    @Test
    fun `ConsoleOutputSink prints to system output`() {
        val originalOut = System.out
        val outputStream = ByteArrayOutputStream()
        System.setOut(PrintStream(outputStream))

        try {
            val outputSink = ConsoleOutputSink()
            outputSink.print("test message")

            val output = outputStream.toString()
            assertEquals("test message\n", output)
        } finally {
            System.setOut(originalOut)
        }
    }
}
