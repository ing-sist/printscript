package parser

import DeclarationAssignmentNode
import DeclarationNode
import TokenType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import parser.dsl.testCode
import parser.dsl.testProgram

/**
 * Tests de declaraciones mejorados con DSL.
 * Cada test muestra claramente qué código está siendo parseado.
 */
class ImprovedDeclarationNodeTest {
    @Test
    fun `should parse simple variable declaration`() {
        testCode("let x: number;")
            .shouldParseSuccessfully()
            .withStatementCount(1)
            .withStatementType(0, DeclarationNode::class.java)
    }

    @Test
    fun `should parse string variable declaration`() {
        val result =
            testCode("let name: string;")
                .shouldParseSuccessfully()
                .withStatementCount(1)

        val declaration = result.getStatement(0) as DeclarationNode
        assertEquals("name", declaration.identifier.name)
        assertEquals(TokenType.StringType, declaration.type.type)
    }

    @Test
    fun `should parse declaration with initialization`() {
        val result =
            testCode("let result: number = 42;")
                .shouldParseSuccessfully()
                .withStatementCount(1)
                .withStatementType(0, DeclarationAssignmentNode::class.java)

        val declaration = result.getStatement(0) as DeclarationAssignmentNode
        assertEquals("result", declaration.identifier.name)
        assertEquals(TokenType.NumberType, declaration.type.type)
    }

    @Test
    fun `should fail parsing invalid declaration without type`() {
        testCode("let x;")
            .shouldFailToParse()
            .withErrorType(ParseError.NoValidParser::class.java)
    }

    @Test
    fun `should parse multiple declarations in a program`() {
        testProgram(
            "let x: number;",
            "let y: string;",
            "let z: number = 10;",
        ).shouldParseSuccessfully()
            .withStatementCount(3)
            .withStatementType(0, DeclarationNode::class.java)
            .withStatementType(1, DeclarationNode::class.java)
            .withStatementType(2, DeclarationAssignmentNode::class.java)
    }
}
