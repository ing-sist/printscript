import FunctionCallNode
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
import validators.FunctionCallValidator

class FunctionCallValidatorTest {
    @Test
    @DisplayName("validates println function call with argument and semicolon")
    fun testValidFunctionCall() {
        val tokens =
            listOf(
                tok(TokenType.FunctionCall, "println"),
                tok(TokenType.LeftParen, "("),
                tok(TokenType.StringLiteral, "\"hi\""),
                tok(TokenType.RightParen, ")"),
                tok(TokenType.Semicolon, ";"),
                tok(TokenType.EOF, "EOF"),
            )
        val result = FunctionCallValidator().validateAndBuild(TestTokenProvider(tokens))
        assertTrue(result is Result.Success)
        val node = (result as Result.Success).value as FunctionCallNode
        assertEquals("println", node.functionName)
        assertTrue(node.isVoid)
    }

    @Test
    @DisplayName("fails when missing semicolon")
    fun testMissingSemicolon() {
        val tokens =
            listOf(
                tok(TokenType.FunctionCall, "println"),
                tok(TokenType.LeftParen, "("),
                tok(TokenType.RightParen, ")"),
                tok(TokenType.EOF, "EOF"),
            )
        val result = FunctionCallValidator().validateAndBuild(TestTokenProvider(tokens))
        assertTrue(result is Result.Failure)
        val err = (result as Result.Failure).errorOrNull()
        assertInstanceOf(ParseError.UnexpectedToken::class.java, err)
    }

    @Test
    @DisplayName("returns Failure(null) for unsupported function names")
    fun testUnsupportedFunctionName() {
        val tokens =
            listOf(
                tok(TokenType.FunctionCall, "foo"),
                tok(TokenType.LeftParen, "("),
                tok(TokenType.RightParen, ")"),
                tok(TokenType.Semicolon, ";"),
                tok(TokenType.EOF, "EOF"),
            )
        val result = FunctionCallValidator().validateAndBuild(TestTokenProvider(tokens))
        assertTrue(result is Result.Failure && result.errorOrNull() == null)
    }
}
