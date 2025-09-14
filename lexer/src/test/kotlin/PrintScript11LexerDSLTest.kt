
import dsl.lexCode11
import dsl.lexProgramWithVersion
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

/**
 * Comprehensive DSL tests for PrintScript 1.1 lexer functionality.
 * Updated to match actual lexer implementation.
 */
class PrintScript11LexerDSLTest {
    @Test
    @DisplayName("Should tokenize const declaration")
    fun testConstDeclaration() {
        lexCode11("const PI: number = 3.14;")
            .shouldTokenizeSuccessfully()
            .withTokensExcludingEOF(
                TokenType.ConstDeclaration,
                TokenType.Identifier,
                TokenType.Colon,
                TokenType.NumberType,
                TokenType.Assignment,
                TokenType.NumberLiteral,
                TokenType.Semicolon,
            ).withTokenAt(0, TokenType.ConstDeclaration, "const")
            .withLexemes("const", "PI", ":", "number", "=", "3.14", ";")
            .endsWithEOF()
    }

    @Test
    @DisplayName("Should tokenize boolean literals")
    fun testBooleanLiterals() {
        lexCode11("let isActive: boolean = true; let isDisabled: boolean = false;")
            .shouldTokenizeSuccessfully()
            .containingTypes(
                TokenType.BooleanType,
                TokenType.BooleanLiteral,
            ).withTokenAt(5, TokenType.BooleanLiteral, "true") // Fixed index
            .withTokenAt(12, TokenType.BooleanLiteral, "false") // Fixed index
            .endsWithEOF()
    }

    @Test
    @DisplayName("Should tokenize if statement with boolean literal")
    fun testIfStatementWithBooleanLiteral() {
        lexCode11("if (true) { println(\"yes\"); }")
            .shouldTokenizeSuccessfully()
            .withTokensExcludingEOF(
                TokenType.Keyword.If,
                TokenType.LeftParen,
                TokenType.BooleanLiteral,
                TokenType.RightParen,
                TokenType.LeftBrace,
                TokenType.FunctionCall,
                TokenType.LeftParen,
                TokenType.StringLiteral,
                TokenType.RightParen,
                TokenType.Semicolon,
                TokenType.RightBrace,
            ).withLexemes("if", "(", "true", ")", "{", "println", "(", "\"yes\"", ")", ";", "}")
            .endsWithEOF()
    }

    @Test
    @DisplayName("Should tokenize if statement with identifier")
    fun testIfStatementWithIdentifier() {
        lexCode11("if (flag) { println(\"active\"); }")
            .shouldTokenizeSuccessfully()
            .withTokensExcludingEOF(
                TokenType.Keyword.If,
                TokenType.LeftParen,
                TokenType.Identifier,
                TokenType.RightParen,
                TokenType.LeftBrace,
                TokenType.FunctionCall,
                TokenType.LeftParen,
                TokenType.StringLiteral,
                TokenType.RightParen,
                TokenType.Semicolon,
                TokenType.RightBrace,
            ).endsWithEOF()
    }

    @Test
    @DisplayName("Should tokenize if-else statement")
    fun testIfElseStatement() {
        lexCode11("if (isValid) { println(\"yes\"); } else { println(\"no\"); }")
            .shouldTokenizeSuccessfully()
            .containingTypes(
                TokenType.Keyword.If,
                TokenType.Keyword.Else,
                TokenType.LeftBrace,
                TokenType.RightBrace,
                TokenType.Identifier,
            ).withTokenCountExcludingEOF(19) // Fixed count
            .endsWithEOF()
    }

    @Test
    @DisplayName("Should tokenize nested braces correctly")
    fun testNestedBraces() {
        lexCode11("if (true) { if (false) { println(\"nested\"); } }")
            .shouldTokenizeSuccessfully()
            .containingTypes(
                TokenType.Keyword.If,
                TokenType.LeftBrace,
                TokenType.RightBrace,
                TokenType.BooleanLiteral,
            ).endsWithEOF()
    }

    @Test
    @DisplayName("Should work with all 1.0 features in 1.1")
    fun testBackwardCompatibility() {
        lexCode11("let name: string = \"John\"; println(name);")
            .shouldTokenizeSuccessfully()
            .withTokensExcludingEOF(
                TokenType.Keyword.VariableDeclaration,
                TokenType.Identifier,
                TokenType.Colon,
                TokenType.StringType,
                TokenType.Assignment,
                TokenType.StringLiteral,
                TokenType.Semicolon,
                TokenType.FunctionCall,
                TokenType.LeftParen,
                TokenType.Identifier,
                TokenType.RightParen,
                TokenType.Semicolon,
            ).endsWithEOF()
    }

    @Test
    @DisplayName("Should tokenize mixed declarations")
    fun testMixedDeclarations() {
        lexCode11("let x: number = 10; const PI: number = 3.14; let flag: boolean = true;")
            .shouldTokenizeSuccessfully()
            .containingTypes(
                TokenType.Keyword.VariableDeclaration,
                TokenType.ConstDeclaration,
                TokenType.BooleanType,
                TokenType.BooleanLiteral,
            ).withTokenCountExcludingEOF(21)
            .endsWithEOF()
    }

    @Test
    @DisplayName("Should handle multi-line if-else blocks")
    fun testMultiLineIfElse() {
        lexProgramWithVersion(
            "1.1",
            "if (condition) {",
            "    let x: number = 42;",
            "    println(x);",
            "} else {",
            "    const msg: string = \"default\";",
            "    println(msg);",
            "}",
        ).shouldTokenizeSuccessfully()
            .containingTypes(
                TokenType.Keyword.If,
                TokenType.Keyword.Else,
                TokenType.Keyword.VariableDeclaration,
                TokenType.ConstDeclaration,
                TokenType.LeftBrace,
                TokenType.RightBrace,
            ).endsWithEOF()
    }

    @Test
    @DisplayName("Should recognize all new 1.1 keywords correctly")
    fun testAll11Keywords() {
        lexCode11("const if else boolean true false")
            .shouldTokenizeSuccessfully()
            .withTokensExcludingEOF(
                TokenType.ConstDeclaration,
                TokenType.Keyword.If,
                TokenType.Keyword.Else,
                TokenType.BooleanType,
                TokenType.BooleanLiteral,
                TokenType.BooleanLiteral,
            ).withLexemes("const", "if", "else", "boolean", "true", "false")
            .endsWithEOF()
    }

    @Test
    @DisplayName("Should handle empty blocks")
    fun testEmptyBlocks() {
        lexCode11("if (true) { } else { }")
            .shouldTokenizeSuccessfully()
            .withTokensExcludingEOF(
                TokenType.Keyword.If,
                TokenType.LeftParen,
                TokenType.BooleanLiteral,
                TokenType.RightParen,
                TokenType.LeftBrace,
                TokenType.RightBrace,
                TokenType.Keyword.Else,
                TokenType.LeftBrace,
                TokenType.RightBrace,
            ).endsWithEOF()
    }

    @Test
    @DisplayName("Should handle boolean type and literals properly")
    fun testBooleanTypeAndLiterals() {
        lexCode11("let flag: boolean = true; const valid: boolean = false;")
            .shouldTokenizeSuccessfully()
            .containingTypes(
                TokenType.BooleanLiteral,
                TokenType.BooleanType,
            ).withTokenAt(3, TokenType.BooleanType, "boolean")
            .withTokenAt(5, TokenType.BooleanLiteral, "true")
            .withTokenAt(10, TokenType.BooleanType, "boolean")
            .withTokenAt(12, TokenType.BooleanLiteral, "false")
            .endsWithEOF()
    }

    @Test
    @DisplayName("Should handle if with boolean variable")
    fun testIfWithBooleanVariable() {
        lexCode11("let isReady: boolean = true; if (isReady) { println(\"ready\"); }")
            .shouldTokenizeSuccessfully()
            .containingTypes(
                TokenType.Keyword.VariableDeclaration,
                TokenType.BooleanType,
                TokenType.BooleanLiteral,
                TokenType.Keyword.If,
                TokenType.Identifier,
            ).endsWithEOF()
    }
}
