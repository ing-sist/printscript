import DeclarationNode
import Result
import TokenType
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import parser.ParseError
import parser.Parser
import util.TestTokenProvider
import util.tok
import validators.provider.DefaultValidatorsProvider

class ParserTest {
    @Test
    @DisplayName("parse returns first valid AST node for a declaration")
    fun testParseSuccess() {
        val tokens =
            listOf(
                tok(TokenType.Keyword.VariableDeclaration, "let"),
                tok(TokenType.Identifier, "x"),
                tok(TokenType.Colon, ":"),
                tok(TokenType.NumberType, "number"),
                tok(TokenType.Semicolon, ";"),
                tok(TokenType.EOF, "EOF"),
            )
        val parser = Parser(DefaultValidatorsProvider())
        val result = parser.parse(TestTokenProvider(tokens))
        assertTrue(result is Result.Success)
        assertInstanceOf(DeclarationNode::class.java, (result as Result.Success).value)
    }

    @Test
    @DisplayName("parse fails with NoValidParser when no validator matches")
    fun testParseNoValidParser() {
        val tokens =
            listOf(
                tok(TokenType.Plus, "+"),
                tok(TokenType.NumberLiteral, "1"),
                tok(TokenType.Semicolon, ";"),
                tok(TokenType.EOF, "EOF"),
            )
        val parser = Parser(DefaultValidatorsProvider())
        val result = parser.parse(TestTokenProvider(tokens))
        assertTrue(result is Result.Failure)
        assertInstanceOf(ParseError.NoValidParser::class.java, (result as Result.Failure).errorOrNull())
    }
}
