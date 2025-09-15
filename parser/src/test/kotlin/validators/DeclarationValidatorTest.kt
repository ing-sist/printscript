import AstNode
import DeclarationNode
import Result
import TokenType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import util.TestTokenProvider
import util.tok
import validators.DeclarationValidator

class DeclarationValidatorTest {
    @Test
    @DisplayName("validates and builds simple let declaration")
    fun testLetDeclaration() {
        val tokens =
            listOf(
                tok(TokenType.Keyword.VariableDeclaration, "let"),
                tok(TokenType.Identifier, "x"),
                tok(TokenType.Colon, ":"),
                tok(TokenType.NumberType, "number"),
                tok(TokenType.Semicolon, ";"),
                tok(TokenType.EOF, "EOF"),
            )
        val stream = TestTokenProvider(tokens)
        val result = DeclarationValidator().validateAndBuild(stream)
        assertTrue(result is Result.Success)
        val node = (result as Result.Success).value
        val decl = assertInstanceOf(DeclarationNode::class.java, node) as DeclarationNode
        assertEquals("x", decl.identifier.name)
        assertEquals(TokenType.NumberType, decl.type.type)
        assertTrue(decl.isMutable)
    }

    @Test
    @DisplayName("returns Failure(null) when pattern doesn't match")
    fun testNonDeclaration() {
        val tokens =
            listOf(
                tok(TokenType.Identifier, "x"),
                tok(TokenType.Assignment, "="),
                tok(TokenType.NumberLiteral, "1"),
                tok(TokenType.Semicolon, ";"),
                tok(TokenType.EOF, "EOF"),
            )
        val stream = TestTokenProvider(tokens)
        val result = DeclarationValidator().validateAndBuild(stream)
        assertTrue(result is Result.Failure && result.errorOrNull() == null)
    }
}
