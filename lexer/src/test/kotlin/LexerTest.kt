import dsl.lexCode
import dsl.lexProgram
import org.junit.jupiter.api.Test

/**
 * Tests del lexer corregidos basándose en la implementación real.
 * Ahora consideran que el lexer agrega EOF automáticamente.
 */
class LexerTest {
    @Test
    fun `should tokenize simple variable declaration and add EOF`() {
        lexCode("let x: number;")
            .shouldTokenizeSuccessfully()
            .withTokenCountExcludingEOF(5) // let, x, :, number, ;
            .withTokensExcludingEOF(
                TokenType.VariableDeclaration,
                TokenType.Identifier,
                TokenType.Colon,
                TokenType.NumberType,
                TokenType.Semicolon,
            ).endsWithEOF()
            .withTokenAt(1, TokenType.Identifier, "x")
    }

    @Test
    fun `should tokenize variable declaration with assignment and add EOF`() {
        lexCode("let name: string = \"John\";")
            .shouldTokenizeSuccessfully()
            .withTokenCountExcludingEOF(7) // Corregido: let, name, :, string, =, "John", ;
            .withTokensExcludingEOF(
                TokenType.VariableDeclaration, // let
                TokenType.Identifier, // name
                TokenType.Colon, // :
                TokenType.StringType, // string
                TokenType.Assignment, // =
                TokenType.StringLiteral, // "John"
                TokenType.Semicolon, // ;
            ).endsWithEOF()
            .withTokenAt(1, TokenType.Identifier, "name")
            .withTokenAt(5, TokenType.StringLiteral, "\"John\"")
    }

    @Test
    fun `should tokenize arithmetic operations and add EOF`() {
        lexCode("result = 10 + 5 * 2;")
            .shouldTokenizeSuccessfully()
            .withTokenCountExcludingEOF(8)
            .withTokensExcludingEOF(
                TokenType.Identifier, // result
                TokenType.Assignment, // =
                TokenType.NumberLiteral, // 10
                TokenType.Plus, // +
                TokenType.NumberLiteral, // 5
                TokenType.Multiply, // *
                TokenType.NumberLiteral, // 2
                TokenType.Semicolon, // ;
            ).endsWithEOF()
    }

    @Test
    fun `should tokenize decimal numbers correctly`() {
        lexCode("pi = 3.14159;")
            .shouldTokenizeSuccessfully()
            .withTokenAt(2, TokenType.NumberLiteral, "3.14159")
            .endsWithEOF()
    }

    @Test
    fun `should tokenize all arithmetic operators`() {
        lexCode("a + b - c * d / e")
            .shouldTokenizeSuccessfully()
            .containingTypes(
                TokenType.Plus,
                TokenType.Minus,
                TokenType.Multiply,
                TokenType.Divide,
                TokenType.EOF,
            ).endsWithEOF()
    }

    @Test
    fun `should tokenize parentheses in expressions`() {
        lexCode("result = (a + b) * (c - d);")
            .shouldTokenizeSuccessfully()
            .containingTypes(
                TokenType.LeftParen,
                TokenType.RightParen,
                TokenType.EOF,
            ).endsWithEOF()
    }

    @Test
    fun `should tokenize println statements`() {
        lexCode("println(\"Hello World\");")
            .shouldTokenizeSuccessfully()
            .withTokenAt(0, TokenType.FunctionCall, "println")
            .withTokenAt(2, TokenType.StringLiteral, "\"Hello World\"")
            .endsWithEOF()
    }

    @Test
    fun `should tokenize single quoted strings`() {
        lexCode("message = 'Single quoted string';")
            .shouldTokenizeSuccessfully()
            .withTokenAt(2, TokenType.StringLiteral, "'Single quoted string'")
            .endsWithEOF()
    }

    @Test
    fun `should tokenize empty string and add EOF`() {
        lexCode("empty = \"\";")
            .shouldTokenizeSuccessfully()
            .withTokenAt(2, TokenType.StringLiteral, "\"\"")
            .endsWithEOF()
    }

    @Test
    fun `should tokenize identifiers with underscores and numbers`() {
        lexCode("let user_name_1: string;")
            .shouldTokenizeSuccessfully()
            .withTokenAt(1, TokenType.Identifier, "user_name_1")
            .endsWithEOF()
    }

    @Test
    fun `should tokenize comparison operators if available`() {
        // Testear operadores de comparación que están en la implementación real
        lexCode("x == y != z")
            .shouldTokenizeSuccessfully()
            .containingTypes(
                TokenType.Identifier,
                TokenType.Equals, // ==
                TokenType.Identifier,
                TokenType.NotEquals, // !=
                TokenType.Identifier,
                TokenType.EOF,
            ).endsWithEOF()
    }

    @Test
    fun `should tokenize complex program with multiple statements`() {
        lexProgram(
            "let name: string = \"Alice\";",
            "let age: number = 25;",
            "println(name);",
        ).shouldTokenizeSuccessfully()
            .containingTypes(
                TokenType.VariableDeclaration,
                TokenType.StringType,
                TokenType.NumberType,
                TokenType.StringLiteral,
                TokenType.NumberLiteral,
                TokenType.FunctionCall,
                TokenType.EOF,
            ).endsWithEOF()
    }

    @Test
    fun `should handle empty input and add EOF`() {
        lexCode("")
            .shouldTokenizeSuccessfully()
            .withTokenCountExcludingEOF(0)
            .withTokenCount(1) // Solo EOF
            .endsWithEOF()
    }

    @Test
    fun `should tokenize with exact lexeme verification`() {
        lexCode("let x = 42;")
            .shouldTokenizeSuccessfully()
            .withLexemes("let", "x", "=", "42", ";")
            .endsWithEOF()
    }

    @Test
    fun `should tokenize complex arithmetic with parentheses`() {
        lexCode("result = (10 + 5) * 2;")
            .shouldTokenizeSuccessfully()
            .containingTypes(
                TokenType.Identifier,
                TokenType.Assignment,
                TokenType.LeftParen,
                TokenType.NumberLiteral,
                TokenType.Plus,
                TokenType.NumberLiteral,
                TokenType.RightParen,
                TokenType.Multiply,
                TokenType.NumberLiteral,
                TokenType.Semicolon,
                TokenType.EOF,
            ).endsWithEOF()
    }

    @Test
    fun `should fail on invalid characters`() {
        lexCode("let x = @invalid;")
            .shouldFailToTokenize()
    }

    @Test
    fun `should fail on unmatched quotes`() {
        lexCode("let message = \"unclosed string;")
            .shouldFailToTokenize()
    }
}
