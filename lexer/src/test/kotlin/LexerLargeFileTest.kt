// Kotlin
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.StringReader
import kotlin.test.fail

/**
 * Test unitario para verificar que el lexer maneja correctamente archivos grandes
 * y no corta tokens como 'println' a 'print'
 */
class LexerLargeFileTest {
    @Test
    fun `test lexer with small println statements`() {
        val input =
            """
            println("test1");
            println("test2");
            println("test3");
            """.trimIndent()

        val tokenRule = RuleGenerator.createTokenRule("1.0")
        val lexer = Lexer(StringReader(input), tokenRule)

        val tokens = mutableListOf<Token>()
        while (true) {
            val token = lexer.nextToken(false, false)
            tokens.add(token)
            if (token.type is TokenType.EOF) break
        }

        // println es FunctionCall en PrintScript 1.0
        val functionCallTokens = tokens.filter { it.type is TokenType.FunctionCall }
        val printlnTokens = functionCallTokens.filter { it.lexeme == "println" }

        assertEquals(3, printlnTokens.size, "Should find 3 println tokens")

        // No debe haber tokens truncados 'print' (serían Identifier)
        val identifierTokens = tokens.filter { it.type is TokenType.Identifier }
        val printTokens = identifierTokens.filter { it.lexeme == "print" }
        assertEquals(0, printTokens.size, "Should not find any truncated 'print' tokens")

        println("✓ Small file test passed - all println tokens complete")
    }

    @Test
    fun `test lexer with medium size file`() {
        val line = "println(\"This is a text\");\n"
        val numberOfLines = 1000
        val input = line.repeat(numberOfLines)

        val tokenRule = RuleGenerator.createTokenRule("1.0")
        val lexer = Lexer(StringReader(input), tokenRule)

        var printlnCount = 0
        var printCount = 0

        while (true) {
            val token = lexer.nextToken(false, false)

            if (token.type is TokenType.FunctionCall && token.lexeme == "println") {
                printlnCount++
            } else if (token.type is TokenType.Identifier && token.lexeme == "print") {
                printCount++
                println("WARNING: Found truncated 'print' token at line ${token.location.line}")
            }

            if (token.type is TokenType.EOF) break
        }

        println("Medium test - println tokens: $printlnCount, truncated: $printCount")

        assertEquals(numberOfLines, printlnCount, "Should find $numberOfLines println tokens")
        assertEquals(0, printCount, "Should not find any truncated tokens")

        println("✓ Medium file test passed")
    }

    @Test
    fun `test lexer with 32K lines like TCK`() {
        val line = "println(\"This is a text\");\n"
        val numberOfLines = 32 * 1024
        val input = line.repeat(numberOfLines)

        println("Testing lexer with $numberOfLines lines...")

        val tokenRule = RuleGenerator.createTokenRule("1.0")
        val lexer = Lexer(StringReader(input), tokenRule)

        var printlnCount = 0
        var printCount = 0
        var tokenCount = 0
        var lastLine = 1

        val startTime = System.currentTimeMillis()

        while (true) {
            val token = lexer.nextToken(false, false)
            tokenCount++

            if (token.type is TokenType.FunctionCall && token.lexeme == "println") {
                printlnCount++
            } else if (token.type is TokenType.Identifier && token.lexeme == "print") {
                printCount++
                println("WARNING: Found truncated 'print' token at line ${token.location.line}")
            }

            lastLine = token.location.line

            // Log progress every 1000 lines
            if (lastLine > 0 && lastLine % 1000 == 0 && token.location.startCol == 1) {
                println("Processed line $lastLine...")
            }

            if (token.type is TokenType.EOF) break
        }

        val endTime = System.currentTimeMillis()

        println("Lexer processing completed in ${endTime - startTime}ms")
        println("Total tokens processed: $tokenCount")
        println("Lines processed: $lastLine")
        println("Complete 'println' tokens: $printlnCount")
        println("Truncated 'print' tokens: $printCount")

        if (printCount > 0) {
            fail("Found $printCount truncated 'print' tokens! This indicates buffer overflow in lexer.")
        }

        assertEquals(numberOfLines, printlnCount, "Should find exactly $numberOfLines complete println tokens")

        println("✓ 32K lines test passed - lexer handles large files correctly")
    }

    @Test
    fun `debug lexer buffer behavior`() {
        val line = "println(\"test\");\n"
        val numberOfLines = 5
        val input = line.repeat(numberOfLines)

        println("Input string length: ${input.length}")
        println("Input content preview:")
        println(input.take(100) + "...")

        val tokenRule = RuleGenerator.createTokenRule("1.0")
        val lexer = Lexer(StringReader(input), tokenRule)

        var tokenCount = 0
        println("\nToken sequence:")

        var eofReached = false
        while (!eofReached && tokenCount <= 100) {
            val token = lexer.nextToken(false, false)
            tokenCount++

            println("Token $tokenCount: ${token.type} = '${token.lexeme}' at line ${token.location.line}")

            eofReached = token.type is TokenType.EOF
        }

        if (!eofReached) {
            println("Breaking after 100 tokens to avoid infinite loop")
        } else {
            println("EOF reached after $tokenCount tokens")
        }

        println("Expected ~${numberOfLines * 4} tokens (println, string, semicolon, newline per line)")
        println("Actual: $tokenCount tokens")
    }
}
