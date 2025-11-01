import TokenType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import parser.ParseError
import util.TestTokenProvider
import util.tok
import validators.helpers.TokenConsumer

class TokenConsumerTest {
    @Test
    @DisplayName("consumes expression up to semicolon handling parentheses")
    fun testConsumeExpression() {
        val tokens =
            listOf(
                tok(TokenType.Identifier, "a"),
                tok(TokenType.Operator.Plus, "+"),
                tok(TokenType.LeftParen, "("),
                tok(TokenType.NumberLiteral, "1"),
                tok(TokenType.Operator.Plus, "+"),
                tok(TokenType.NumberLiteral, "2"),
                tok(TokenType.RightParen, ")"),
                tok(TokenType.Semicolon, ";"),
                tok(TokenType.EOF, "EOF"),
            )
        val stream = TestTokenProvider(tokens)
        val consumed = TokenConsumer.consumeExpressionAndSemicolon(stream)
        // It should consume from current index to and including the semicolon (8 tokens before EOF)
        assertEquals(8, consumed.size)
        assertEquals(TokenType.Semicolon, consumed.last().type)
    }

    @Test
    @DisplayName("throws when reaching EOF before semicolon")
    fun testEOFBeforeSemicolon() {
        val tokens =
            listOf(
                tok(TokenType.Identifier, "a"),
                tok(TokenType.Operator.Plus, "+"),
                tok(TokenType.NumberLiteral, "1"),
                tok(TokenType.EOF, "EOF"),
            )
        val stream = TestTokenProvider(tokens)
        assertThrows(ParseError.UnexpectedToken::class.java) {
            TokenConsumer.consumeExpressionAndSemicolon(stream)
        }
    }
}
