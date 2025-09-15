import AstNode
import ConditionalNode
import Result
import TokenType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import parser.ParseError
import util.TestTokenProvider
import util.tok
import validators.IfValidator
import validators.provider.DefaultValidatorsProvider

class IfValidatorTest {
    private fun provider() = DefaultValidatorsProvider()

    @Test
    @DisplayName("parses if-then with single statement")
    fun testIfThen() {
        val tokens =
            listOf(
                tok(TokenType.Keyword.If, "if"),
                tok(TokenType.LeftParen, "("),
                tok(TokenType.BooleanLiteral, "true"),
                tok(TokenType.RightParen, ")"),
                tok(TokenType.LeftBrace, "{"),
                tok(TokenType.FunctionCall, "println"),
                tok(TokenType.LeftParen, "("),
                tok(TokenType.StringLiteral, "\"a\""),
                tok(TokenType.RightParen, ")"),
                tok(TokenType.Semicolon, ";"),
                tok(TokenType.RightBrace, "}"),
                tok(TokenType.EOF, "EOF"),
            )
        val result = IfValidator(provider()).validateAndBuild(TestTokenProvider(tokens))
        assertTrue(result is Result.Success)
        val node = (result as Result.Success).value as ConditionalNode
        assertEquals(1, node.thenBody.size)
        assertEquals(null, node.elseBody)
    }

    @Test
    @DisplayName("parses if-then-else with statements")
    fun testIfThenElse() {
        val tokens =
            listOf(
                tok(TokenType.Keyword.If, "if"),
                tok(TokenType.LeftParen, "("),
                tok(TokenType.Identifier, "cond"),
                tok(TokenType.RightParen, ")"),
                tok(TokenType.LeftBrace, "{"),
                tok(TokenType.FunctionCall, "println"),
                tok(TokenType.LeftParen, "("),
                tok(TokenType.StringLiteral, "\"t\""),
                tok(TokenType.RightParen, ")"),
                tok(TokenType.Semicolon, ";"),
                tok(TokenType.RightBrace, "}"),
                tok(TokenType.Keyword.Else, "else"),
                tok(TokenType.LeftBrace, "{"),
                tok(TokenType.FunctionCall, "println"),
                tok(TokenType.LeftParen, "("),
                tok(TokenType.StringLiteral, "\"f\""),
                tok(TokenType.RightParen, ")"),
                tok(TokenType.Semicolon, ";"),
                tok(TokenType.RightBrace, "}"),
                tok(TokenType.EOF, "EOF"),
            )
        val result = IfValidator(provider()).validateAndBuild(TestTokenProvider(tokens))
        assertTrue(result is Result.Success)
        val node = (result as Result.Success).value as ConditionalNode
        assertEquals(1, node.thenBody.size)
        assertEquals(1, node.elseBody?.size)
    }

    @Test
    @DisplayName("fails when missing left parenthesis after if")
    fun testMissingLeftParen() {
        val tokens =
            listOf(
                tok(TokenType.Keyword.If, "if"),
                tok(TokenType.Identifier, "x"),
                tok(TokenType.EOF, "EOF"),
            )
        val result = IfValidator(provider()).validateAndBuild(TestTokenProvider(tokens))
        assertTrue(result is Result.Failure)
        assertInstanceOf(ParseError.UnexpectedToken::class.java, (result as Result.Failure).errorOrNull())
    }

    @Test
    @DisplayName("fails when condition is empty")
    fun testEmptyCondition() {
        val tokens =
            listOf(
                tok(TokenType.Keyword.If, "if"),
                tok(TokenType.LeftParen, "("),
                tok(TokenType.RightParen, ")"),
                tok(TokenType.LeftBrace, "{"),
                tok(TokenType.RightBrace, "}"),
                tok(TokenType.EOF, "EOF"),
            )
        val result = IfValidator(provider()).validateAndBuild(TestTokenProvider(tokens))
        assertTrue(result is Result.Failure)
        val err = (result as Result.Failure).errorOrNull()
        val inv = assertInstanceOf(ParseError.InvalidSyntax::class.java, err) as ParseError.InvalidSyntax
        assertTrue(inv.reason.contains("no puede estar vacía"))
    }

    @Test
    @DisplayName("fails when then block is missing '{'")
    fun testMissingThenLeftBrace() {
        val tokens =
            listOf(
                tok(TokenType.Keyword.If, "if"),
                tok(TokenType.LeftParen, "("),
                tok(TokenType.BooleanLiteral, "true"),
                tok(TokenType.RightParen, ")"),
                tok(TokenType.FunctionCall, "println"),
                tok(TokenType.LeftParen, "("),
                tok(TokenType.RightParen, ")"),
                tok(TokenType.Semicolon, ";"),
                tok(TokenType.EOF, "EOF"),
            )
        val result = IfValidator(provider()).validateAndBuild(TestTokenProvider(tokens))
        assertTrue(result is Result.Failure)
        val err = (result as Result.Failure).errorOrNull()
        val inv = assertInstanceOf(ParseError.InvalidSyntax::class.java, err) as ParseError.InvalidSyntax
        assertTrue(inv.reason.contains("then"))
    }

    @Test
    @DisplayName("fails when else block is missing '{'")
    fun testMissingElseLeftBrace() {
        val tokens =
            listOf(
                tok(TokenType.Keyword.If, "if"),
                tok(TokenType.LeftParen, "("),
                tok(TokenType.BooleanLiteral, "true"),
                tok(TokenType.RightParen, ")"),
                tok(TokenType.LeftBrace, "{"),
                tok(TokenType.RightBrace, "}"),
                tok(TokenType.Keyword.Else, "else"),
                tok(TokenType.FunctionCall, "println"),
                tok(TokenType.LeftParen, "("),
                tok(TokenType.RightParen, ")"),
                tok(TokenType.Semicolon, ";"),
                tok(TokenType.EOF, "EOF"),
            )
        val result = IfValidator(provider()).validateAndBuild(TestTokenProvider(tokens))
        assertTrue(result is Result.Failure)
        val err = (result as Result.Failure).errorOrNull()
        val inv = assertInstanceOf(ParseError.InvalidSyntax::class.java, err) as ParseError.InvalidSyntax
        assertTrue(inv.reason.contains("else"))
    }

    @Test
    @DisplayName("fails when block doesn't close with '}'")
    fun testMissingRightBrace() {
        val tokens =
            listOf(
                tok(TokenType.Keyword.If, "if"),
                tok(TokenType.LeftParen, "("),
                tok(TokenType.BooleanLiteral, "true"),
                tok(TokenType.RightParen, ")"),
                tok(TokenType.LeftBrace, "{"),
                tok(TokenType.FunctionCall, "println"),
                tok(TokenType.LeftParen, "("),
                tok(TokenType.StringLiteral, "\"x\""),
                tok(TokenType.RightParen, ")"),
                tok(TokenType.Semicolon, ";"),
                tok(TokenType.EOF, "EOF"),
            )
        val result = IfValidator(provider()).validateAndBuild(TestTokenProvider(tokens))
        assertTrue(result is Result.Failure)
    }
}
