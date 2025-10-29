import dsl.lexCode
import dsl.lexCode10
import dsl.lexCode11
import dsl.lexCodeWithVersion
import dsl.lexProgram
import dsl.lexProgramWithVersion
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

/**
 * Advanced DSL tests showcasing all lexer capabilities and DSL features.
 */
class AdvancedLexerDSLTest {
    @Test
    @DisplayName("Should demonstrate complete DSL fluent interface")
    fun testCompleteFluentInterface() {
        lexCode11("const greeting: string = \"Hello\"; if (true) { println(greeting); }")
            .shouldTokenizeSuccessfully()
            .withTokenCountExcludingEOF(18) // Fixed count
            .withTokenCount(19) // Including EOF
            .containingTypes(TokenType.Keyword.ConstDeclaration, TokenType.BooleanLiteral, TokenType.Keyword.If)
            .withTokenAt(0, TokenType.Keyword.ConstDeclaration, "const")
            .withTokenAt(5, TokenType.StringLiteral, "\"Hello\"")
            .withTokenAt(9, TokenType.BooleanLiteral, "true")
            .endsWithEOF()
    }

    @Test
    @DisplayName("Should handle real-world PrintScript 1.1 program")
    fun testRealWorldProgram() {
        lexProgramWithVersion(
            "1.1",
            "const PI: number = 3.14159;",
            "let radius: number = 5;",
            "let area: number = PI * radius * radius;",
        ).shouldTokenizeSuccessfully()
            .containingTypes(
                TokenType.Keyword.ConstDeclaration,
                TokenType.Keyword.VariableDeclaration,
                TokenType.NumberLiteral,
                TokenType.Operator.Multiply,
            ).endsWithEOF()
    }

    @Test
    @DisplayName("Should compare tokenization between versions")
    fun testVersionComparison() {
        val simpleCode = "let x: number = 42;"

        // Both versions should handle basic code identically
        val result10 = lexCode10(simpleCode).shouldTokenizeSuccessfully()
        val result11 = lexCode11(simpleCode).shouldTokenizeSuccessfully()

        result10.withTokenCountExcludingEOF(7) // Fixed count
        result11.withTokenCountExcludingEOF(7) // Fixed count

        // But version-specific features should differ
        lexCode10("const x = 5;")
            .shouldTokenizeSuccessfully()
            .withTokenAt(0, TokenType.Identifier, "const") // treated as identifier in 1.0

        lexCode11("const x: number = 5;")
            .shouldTokenizeSuccessfully()
            .withTokenAt(0, TokenType.Keyword.ConstDeclaration, "const") // proper keyword in 1.1
    }

    @Test
    @DisplayName("Should handle edge cases with DSL")
    fun testEdgeCases() {
        // Empty code
        lexCode("")
            .shouldTokenizeSuccessfully()
            .withTokenCount(1)
            .endsWithEOF()

        // Only whitespace
        lexCode("   \n\t  ")
            .shouldTokenizeSuccessfully()
            .withTokenCount(1)
            .endsWithEOF()

        // Simple string test
        lexCode10("let msg: string = \"hello\";")
            .shouldTokenizeSuccessfully()
            .withTokenAt(5, TokenType.StringLiteral, "\"hello\"")
            .endsWithEOF()
    }

    @Test
    @DisplayName("Should validate token positions and lexemes precisely")
    fun testPreciseTokenValidation() {
        lexCode11("const flag: boolean = true;")
            .shouldTokenizeSuccessfully()
            .withTokenAt(0, TokenType.Keyword.ConstDeclaration, "const")
            .withTokenAt(1, TokenType.Identifier, "flag")
            .withTokenAt(2, TokenType.Colon, ":")
            .withTokenAt(3, TokenType.BooleanType, "boolean")
            .withTokenAt(4, TokenType.Assignment, "=")
            .withTokenAt(5, TokenType.BooleanLiteral, "true")
            .withTokenAt(6, TokenType.Semicolon, ";")
            .getTokensExcludingEOF()
            .also { tokens ->
                assert(tokens.size == 7)
                assert(tokens[0].lexeme == "const")
                assert(tokens[1].lexeme == "flag")
                assert(tokens[5].lexeme == "true")
            }
    }

    @Test
    @DisplayName("Should demonstrate all DSL convenience functions")
    fun testAllDSLFunctions() {
        // Basic function
        lexCode("let x: number = 5;")
            .shouldTokenizeSuccessfully()
            .endsWithEOF()

        // Multi-line function
        lexProgram(
            "let x: number = 1;",
            "let y: number = 2;",
        ).shouldTokenizeSuccessfully()
            .withTokenCountExcludingEOF(14) // Fixed count: 7 + 7 tokens
            .endsWithEOF()

        // Version-specific functions
        lexCode10("let name: string = \"John\";")
            .shouldTokenizeSuccessfully()
            .endsWithEOF()

        lexCode11("const PI: number = 3.14;")
            .shouldTokenizeSuccessfully()
            .endsWithEOF()

        // Version with parameters function
        lexCodeWithVersion("let test: string = \"version\";", "1.0")
            .shouldTokenizeSuccessfully()
            .endsWithEOF()

        // Multi-line with version
        lexProgramWithVersion(
            "1.1",
            "if (true) {",
            "    println(\"test\");",
            "}",
        ).shouldTokenizeSuccessfully()
            .containingTypes(TokenType.Keyword.If, TokenType.LeftBrace, TokenType.RightBrace)
            .endsWithEOF()
    }

    @Test
    @DisplayName("Should handle complex nested structures")
    fun testComplexNestedStructures() {
        lexCode11(
            """
            if (condition1) {
                if (condition2) {
                    const result: boolean = true;
                    println(result);
                } else {
                    let temp: number = 42;
                    println(temp);
                }
            }
            """.trimIndent(),
        ).shouldTokenizeSuccessfully()
            .containingTypes(
                TokenType.Keyword.If,
                TokenType.Keyword.Else,
                TokenType.Keyword.ConstDeclaration,
                TokenType.Keyword.VariableDeclaration,
                TokenType.BooleanType,
                TokenType.BooleanLiteral,
                TokenType.LeftBrace,
                TokenType.RightBrace,
            ).endsWithEOF()
    }
}
