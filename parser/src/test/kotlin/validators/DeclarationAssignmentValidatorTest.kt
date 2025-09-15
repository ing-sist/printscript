import DeclarationAssignmentNode
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
import validators.DeclarationAssignmentValidator

class DeclarationAssignmentValidatorTest {
    @Test
    @DisplayName("validates and builds declaration with assignment")
    fun testDeclarationAssignment() {
        val tokens =
            listOf(
                tok(TokenType.Keyword.VariableDeclaration, "let"),
                tok(TokenType.Identifier, "x"),
                tok(TokenType.Colon, ":"),
                tok(TokenType.NumberType, "number"),
                tok(TokenType.Assignment, "="),
                tok(TokenType.NumberLiteral, "3"),
                tok(TokenType.Plus, "+"),
                tok(TokenType.NumberLiteral, "4"),
                tok(TokenType.Semicolon, ";"),
                tok(TokenType.EOF, "EOF"),
            )
        val result = DeclarationAssignmentValidator().validateAndBuild(TestTokenProvider(tokens))
        assertTrue(result is Result.Success)
        val node = (result as Result.Success).value as DeclarationAssignmentNode
        val decl = assertInstanceOf(DeclarationNode::class.java, node.declaration) as DeclarationNode
        assertEquals("x", decl.identifier.name)
    }

    @Test
    @DisplayName("returns Failure(null) when no match")
    fun testNoMatch() {
        val tokens =
            listOf(
                tok(TokenType.Keyword.VariableDeclaration, "let"),
                tok(TokenType.Identifier, "a"),
                tok(TokenType.Colon, ":"),
                tok(TokenType.NumberType, "number"),
                tok(TokenType.Semicolon, ";"),
                tok(TokenType.EOF, "EOF"),
            )
        val result = DeclarationAssignmentValidator().validateAndBuild(TestTokenProvider(tokens))
        assertTrue(result is Result.Failure && result.errorOrNull() == null)
    }
}
