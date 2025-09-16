import AssignmentNode
import DeclarationAssignmentNode
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
                // let x : number = readEnv("PORT");
                tok(TokenType.Keyword.VariableDeclaration, "let"),
                tok(TokenType.Identifier, "x"),
                tok(TokenType.Colon, ":"),
                tok(TokenType.Keyword.NumberType, "number"),
                tok(TokenType.Assignment, "="),
                tok(TokenType.FunctionCall, "readEnv"),
                tok(TokenType.LeftParen, "("),
                tok(TokenType.StringLiteral, "\"PORT\""),
                tok(TokenType.RightParen, ")"),
                tok(TokenType.Semicolon, ";"),
                tok(TokenType.EOF, "EOF"),
            )
        val parser = Parser(DefaultValidatorsProvider())
        val result = parser.parse(TestTokenProvider(tokens))
        assertTrue(result is Result.Success)
        assertInstanceOf(DeclarationAssignmentNode::class.java, (result as Result.Success).value)
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

    @Test
    @DisplayName("parse readInput in string concatenation expression")
    fun testReadInputInExpression() {
        val tokens =
            listOf(
                tok(TokenType.Keyword.VariableDeclaration, "let"),
                tok(TokenType.Identifier, "result"),
                tok(TokenType.Colon, ":"),
                tok(TokenType.Keyword.StringType, "string"),
                tok(TokenType.Assignment, "="),
                tok(TokenType.FunctionCall, "readInput"),
                tok(TokenType.LeftParen, "("),
                tok(TokenType.StringLiteral, "\"Enter your name: \""),
                tok(TokenType.RightParen, ")"),
                tok(TokenType.Plus, "+"),
                tok(TokenType.StringLiteral, "\" is your name\""),
                tok(TokenType.Semicolon, ";"),
                tok(TokenType.EOF, "EOF"),
            )
        val parser = Parser(DefaultValidatorsProvider())
        val result = parser.parse(TestTokenProvider(tokens))
        assertTrue(result is Result.Success)
        assertInstanceOf(DeclarationAssignmentNode::class.java, (result as Result.Success).value)
    }

    @Test
    @DisplayName("parse readEnv in numeric expression")
    fun testReadEnvInNumericExpression() {
        val tokens =
            listOf(
                tok(TokenType.Keyword.VariableDeclaration, "let"),
                tok(TokenType.Identifier, "port"),
                tok(TokenType.Colon, ":"),
                tok(TokenType.Keyword.NumberType, "number"),
                tok(TokenType.Assignment, "="),
                tok(TokenType.FunctionCall, "readEnv"),
                tok(TokenType.LeftParen, "("),
                tok(TokenType.StringLiteral, "\"PORT\""),
                tok(TokenType.RightParen, ")"),
                tok(TokenType.Plus, "+"),
                tok(TokenType.NumberLiteral, "8000"),
                tok(TokenType.Semicolon, ";"),
                tok(TokenType.EOF, "EOF"),
            )
        val parser = Parser(DefaultValidatorsProvider())
        val result = parser.parse(TestTokenProvider(tokens))
        assertTrue(result is Result.Success)
        assertInstanceOf(DeclarationAssignmentNode::class.java, (result as Result.Success).value)
    }

    @Test
    @DisplayName("parse readInput in complex expression with multiple operations")
    fun testReadInputInComplexExpression() {
        val tokens =
            listOf(
                tok(TokenType.Keyword.VariableDeclaration, "let"),
                tok(TokenType.Identifier, "message"),
                tok(TokenType.Colon, ":"),
                tok(TokenType.Keyword.StringType, "string"),
                tok(TokenType.Assignment, "="),
                tok(TokenType.StringLiteral, "\"Hello \""),
                tok(TokenType.Plus, "+"),
                tok(TokenType.FunctionCall, "readInput"),
                tok(TokenType.LeftParen, "("),
                tok(TokenType.StringLiteral, "\"Enter name: \""),
                tok(TokenType.RightParen, ")"),
                tok(TokenType.Plus, "+"),
                tok(TokenType.StringLiteral, "\"!\""),
                tok(TokenType.Semicolon, ";"),
                tok(TokenType.EOF, "EOF"),
            )
        val parser = Parser(DefaultValidatorsProvider())
        val result = parser.parse(TestTokenProvider(tokens))
        assertTrue(result is Result.Success)
        assertInstanceOf(DeclarationAssignmentNode::class.java, (result as Result.Success).value)
    }

    @Test
    @DisplayName("parse readEnv in assignment to existing variable")
    fun testReadEnvInAssignment() {
        val tokens =
            listOf(
                tok(TokenType.Identifier, "x"),
                tok(TokenType.Assignment, "="),
                tok(TokenType.FunctionCall, "readEnv"),
                tok(TokenType.LeftParen, "("),
                tok(TokenType.StringLiteral, "\"DATABASE_URL\""),
                tok(TokenType.RightParen, ")"),
                tok(TokenType.Plus, "+"),
                tok(TokenType.StringLiteral, "\"/api\""),
                tok(TokenType.Semicolon, ";"),
                tok(TokenType.EOF, "EOF"),
            )
        val parser = Parser(DefaultValidatorsProvider())
        val result = parser.parse(TestTokenProvider(tokens))
        assertTrue(result is Result.Success)
        assertInstanceOf(AssignmentNode::class.java, (result as Result.Success).value)
    }
}
