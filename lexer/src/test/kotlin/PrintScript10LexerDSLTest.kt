
import dsl.lexCode10
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.DisplayName

/**
 * Comprehensive DSL tests for PrintScript 1.0 lexer functionality.
 * Updated to match the actual lexer implementation.
 */
class PrintScript10LexerDSLTest {

    @Test
    @DisplayName("Should tokenize basic variable declaration")
    fun testBasicVariableDeclaration() {
        lexCode10("let x: number;")
            .shouldTokenizeSuccessfully()
            .withTokensExcludingEOF(
                TokenType.VariableDeclaration,
                TokenType.Identifier,
                TokenType.Colon,
                TokenType.NumberType,
                TokenType.Semicolon
            )
            .withLexemes("let", "x", ":", "number", ";")
            .endsWithEOF()
    }

    @Test
    @DisplayName("Should tokenize variable declaration with assignment")
    fun testVariableDeclarationWithAssignment() {
        lexCode10("let name: string = \"John\";")
            .shouldTokenizeSuccessfully()
            .withTokenCountExcludingEOF(7)
            .withTokenAt(0, TokenType.VariableDeclaration, "let")
            .withTokenAt(1, TokenType.Identifier, "name")
            .withTokenAt(2, TokenType.Colon, ":")
            .withTokenAt(3, TokenType.StringType, "string")
            .withTokenAt(4, TokenType.Assignment, "=")
            .withTokenAt(5, TokenType.StringLiteral, "\"John\"")
            .withTokenAt(6, TokenType.Semicolon, ";")
            .endsWithEOF()
    }

    @Test
    @DisplayName("Should tokenize arithmetic operations")
    fun testArithmeticOperations() {
        lexCode10("let result: number = 10 + 5 * 2;")
            .shouldTokenizeSuccessfully()
            .containingTypes(
                TokenType.VariableDeclaration,
                TokenType.NumberLiteral,
                TokenType.Plus,
                TokenType.Multiply
            )
            .withTokenCountExcludingEOF(11)
            .endsWithEOF()
    }

    @Test
    @DisplayName("Should tokenize function call")
    fun testFunctionCall() {
        lexCode10("println(\"Hello World\");")
            .shouldTokenizeSuccessfully()
            .withTokensExcludingEOF(
                TokenType.FunctionCall,
                TokenType.LeftParen,
                TokenType.StringLiteral,
                TokenType.RightParen,
                TokenType.Semicolon
            )
            .withLexemes("println", "(", "\"Hello World\"", ")", ";")
            .endsWithEOF()
    }

    @Test
    @DisplayName("Should tokenize single and double quoted strings")
    fun testStringLiterals() {
        lexCode10("let msg1: string = \"double\"; let msg2: string = 'single';")
            .shouldTokenizeSuccessfully()
            .containingTypes(TokenType.StringLiteral)
            .withTokenAt(5, TokenType.StringLiteral, "\"double\"")
            .withTokenAt(12, TokenType.StringLiteral, "'single'")
            .endsWithEOF()
    }

    @Test
    @DisplayName("Should tokenize decimal numbers")
    fun testDecimalNumbers() {
        lexCode10("let pi: number = 3.14159;")
            .shouldTokenizeSuccessfully()
            .withTokenAt(4, TokenType.Assignment, "=")
            .withTokenAt(5, TokenType.NumberLiteral, "3.14159")
            .withTokenCountExcludingEOF(7)
            .endsWithEOF()
    }

    @Test
    @DisplayName("Should tokenize arithmetic expressions with parentheses")
    fun testArithmeticExpressions() {
        lexCode10("let calc: number = (10 + 5) / 2;")
            .shouldTokenizeSuccessfully()
            .containingTypes(
                TokenType.LeftParen,
                TokenType.RightParen,
                TokenType.Plus,
                TokenType.Divide
            )
            .withTokenCountExcludingEOF(13)
            .endsWithEOF()
    }

    @Test
    @DisplayName("Should not recognize 1.1 keywords in 1.0")
    fun testVersion10DoesNotRecognize11Keywords() {
        // In 1.0, 'const' should be treated as identifier
        lexCode10("const x = 5;")
            .shouldTokenizeSuccessfully()
            .withTokenAt(0, TokenType.Identifier, "const")
            .notContaining(TokenType.ConstDeclaration)
    }

    @Test
    @DisplayName("Should handle empty input")
    fun testEmptyInput() {
        lexCode10("")
            .shouldTokenizeSuccessfully()
            .withTokenCount(1) // Only EOF
            .endsWithEOF()
    }

    @Test
    @DisplayName("Should handle whitespace-only input")
    fun testWhitespaceOnlyInput() {
        lexCode10("   \n\t  ")
            .shouldTokenizeSuccessfully()
            .withTokenCount(1) // Only EOF (whitespace is filtered out)
            .endsWithEOF()
    }

    @Test
    @DisplayName("Should handle all arithmetic operators")
    fun testAllArithmeticOperators() {
        lexCode10("let result: number = a + b - c * d / e;")
            .shouldTokenizeSuccessfully()
            .containingTypes(
                TokenType.Plus,
                TokenType.Minus,
                TokenType.Multiply,
                TokenType.Divide
            )
            .endsWithEOF()
    }
}
