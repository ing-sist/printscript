package integration

import ConditionalNode
import DeclarationAssignmentNode
import Result
import TokenType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import parser.ParseError
import parser.Parser
import util.TestTokenProvider
import util.tok
import validators.provider.DefaultValidatorsProvider
import validators.provider.ValidatorsFactory

/**
 * Integration test for nested if statements.
 * Tests the exact example from the requirements:
 *
 * let flag : boolean = false;
 * if (true) {
 *     if(flag) {
 *         println("Its false");
 *     }
 *     else {
 *         if (true){
 *             println("PERFECT");
 *         }
 *     }
 * }
 */
class NestedIfIntegrationTest {
    @Test
    @DisplayName("Full nested if example: declaration + nested if statements")
    fun testFullNestedIfExample() {
        // Test the complete nested if example
        val parser = Parser(DefaultValidatorsProvider("1.1"))

        // First statement: let flag : boolean = false;
        val declarationTokens =
            listOf(
                tok(TokenType.Keyword.VariableDeclaration, "let"),
                tok(TokenType.Identifier, "flag"),
                tok(TokenType.Colon, ":"),
                tok(TokenType.BooleanType, "boolean"),
                tok(TokenType.Assignment, "="),
                tok(TokenType.BooleanLiteral, "false"),
                tok(TokenType.Semicolon, ";"),
                tok(TokenType.EOF, ""),
            )

        val declarationResult = parser.parse(TestTokenProvider(declarationTokens))
        assertTrue(declarationResult is Result.Success, "Declaration should parse successfully")
        assertInstanceOf(DeclarationAssignmentNode::class.java, (declarationResult as Result.Success).value)

        // Second statement: nested if with three levels
        val nestedIfTokens =
            listOf(
                // Outer if (true)
                tok(TokenType.Keyword.If, "if"),
                tok(TokenType.LeftParen, "("),
                tok(TokenType.BooleanLiteral, "true"),
                tok(TokenType.RightParen, ")"),
                tok(TokenType.LeftBrace, "{"),
                // First nested if(flag)
                tok(TokenType.Keyword.If, "if"),
                tok(TokenType.LeftParen, "("),
                tok(TokenType.Identifier, "flag"),
                tok(TokenType.RightParen, ")"),
                tok(TokenType.LeftBrace, "{"),
                tok(TokenType.FunctionCall, "println"),
                tok(TokenType.LeftParen, "("),
                tok(TokenType.StringLiteral, "Its false"),
                tok(TokenType.RightParen, ")"),
                tok(TokenType.Semicolon, ";"),
                tok(TokenType.RightBrace, "}"),
                // else block
                tok(TokenType.Keyword.Else, "else"),
                tok(TokenType.LeftBrace, "{"),
                // Second nested if (true)
                tok(TokenType.Keyword.If, "if"),
                tok(TokenType.LeftParen, "("),
                tok(TokenType.BooleanLiteral, "true"),
                tok(TokenType.RightParen, ")"),
                tok(TokenType.LeftBrace, "{"),
                tok(TokenType.FunctionCall, "println"),
                tok(TokenType.LeftParen, "("),
                tok(TokenType.StringLiteral, "PERFECT"),
                tok(TokenType.RightParen, ")"),
                tok(TokenType.Semicolon, ";"),
                tok(TokenType.RightBrace, "}"),
                // Close else block
                tok(TokenType.RightBrace, "}"),
                // Close outer if
                tok(TokenType.RightBrace, "}"),
                tok(TokenType.EOF, ""),
            )

        val nestedIfResult = parser.parse(TestTokenProvider(nestedIfTokens))
        assertTrue(nestedIfResult is Result.Success, "Nested if should parse successfully")

        val outerIf = (nestedIfResult as Result.Success).value as ConditionalNode
        assertEquals(1, outerIf.thenBody.size, "Outer if should have one statement in then body")

        // Verify the nested if in the then body
        val firstNestedIf = outerIf.thenBody[0] as ConditionalNode
        assertEquals(1, firstNestedIf.thenBody.size, "First nested if should have println in then body")
        assertEquals(1, firstNestedIf.elseBody?.size ?: 0, "First nested if should have else body")

        // Verify the second nested if in the else body
        val secondNestedIf = firstNestedIf.elseBody?.get(0) as ConditionalNode
        assertEquals(1, secondNestedIf.thenBody.size, "Second nested if should have println in then body")
    }

    @Test
    @DisplayName("Version 1.0 does not support if statements")
    fun testV10DoesNotSupportIf() {
        val parser = Parser(DefaultValidatorsProvider("1.0"))

        val ifTokens =
            listOf(
                tok(TokenType.Keyword.If, "if"),
                tok(TokenType.LeftParen, "("),
                tok(TokenType.BooleanLiteral, "true"),
                tok(TokenType.RightParen, ")"),
                tok(TokenType.LeftBrace, "{"),
                tok(TokenType.RightBrace, "}"),
                tok(TokenType.EOF, ""),
            )

        val result = parser.parse(TestTokenProvider(ifTokens))
        assertTrue(result is Result.Failure, "Version 1.0 should not support if statements")
        assertInstanceOf(ParseError.NoValidParser::class.java, (result as Result.Failure).errorOrNull())
    }

    @Test
    @DisplayName("Version 1.1 supports if statements")
    fun testV11SupportsIf() {
        val validators = ValidatorsFactory.createValidators("1.1")

        // Verify that version 1.1 includes all validators including IfValidator
        assertTrue(validators.size > 4, "Version 1.1 should have more validators than version 1.0")

        // Test that if statements can be parsed
        val parser = Parser(DefaultValidatorsProvider("1.1"))

        val ifTokens =
            listOf(
                tok(TokenType.Keyword.If, "if"),
                tok(TokenType.LeftParen, "("),
                tok(TokenType.BooleanLiteral, "true"),
                tok(TokenType.RightParen, ")"),
                tok(TokenType.LeftBrace, "{"),
                tok(TokenType.FunctionCall, "println"),
                tok(TokenType.LeftParen, "("),
                tok(TokenType.StringLiteral, "hello"),
                tok(TokenType.RightParen, ")"),
                tok(TokenType.Semicolon, ";"),
                tok(TokenType.RightBrace, "}"),
                tok(TokenType.EOF, ""),
            )

        val result = parser.parse(TestTokenProvider(ifTokens))
        assertTrue(result is Result.Success, "Version 1.1 should support if statements")
        assertInstanceOf(ConditionalNode::class.java, (result as Result.Success).value)
    }
}
