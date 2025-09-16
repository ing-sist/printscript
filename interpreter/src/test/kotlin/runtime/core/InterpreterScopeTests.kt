package runtime.core

import AssignmentNode
import ConditionalNode
import DeclarationAssignmentNode
import DeclarationNode
import FunctionCallNode
import IdentifierNode
import LiteralNode
import Location
import Token
import TokenType
import language.errors.UndeclaredVariableError
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import runtime.providers.BufferedOutputSink
import runtime.providers.MapEnvProvider
import runtime.providers.ProgrammaticInputProvider

class InterpreterScopeTests {
    private lateinit var interpreter: Interpreter
    private lateinit var outputSink: BufferedOutputSink

    @BeforeEach
    fun setUp() {
        outputSink = BufferedOutputSink()
        interpreter =
            InterpreterRuntimeFactory().createRuntime(
                ProgrammaticInputProvider(mutableListOf()),
                MapEnvProvider(emptyMap()),
                outputSink,
            )
    }

    @Test
    fun `should handle variable shadowing in if scope`() {
        // let x: string = "outer";
        val outerDeclaration = createStringDeclaration("x", "outer")
        interpreter.execute(outerDeclaration)

        // if (true) {
        //   let x: string = "inner";
        //   printLn(x); // Should print "inner"
        // }
        val innerDeclaration = createStringDeclaration("x", "inner")
        val printInner = createPrintLnCall("x")
        val conditional =
            ConditionalNode(
                createBooleanLiteral(true),
                listOf(innerDeclaration, printInner),
                null,
            )
        interpreter.execute(conditional)

        // printLn(x); // Should print "outer" - outer scope restored
        val printOuter = createPrintLnCall("x")
        interpreter.execute(printOuter)

        assertEquals(listOf("inner", "outer"), outputSink.getOutput())
    }

    @Test
    fun `should access outer scope variables from inner scope`() {
        // let message: string = "Hello from outer";
        val outerDeclaration = createStringDeclaration("message", "Hello from outer")
        interpreter.execute(outerDeclaration)

        // if (true) {
        //   printLn(message); // Should access outer variable
        // }
        val printMessage = createPrintLnCall("message")
        val conditional =
            ConditionalNode(
                createBooleanLiteral(true),
                listOf(printMessage),
                null,
            )
        interpreter.execute(conditional)

        assertEquals(listOf("Hello from outer"), outputSink.getOutput())
    }

    @Test
    fun `should not access inner scope variables from outer scope`() {
        // if (true) {
        //   let innerVar: string = "only in inner";
        // }
        val innerDeclaration = createStringDeclaration("innerVar", "only in inner")
        val conditional =
            ConditionalNode(
                createBooleanLiteral(true),
                listOf(innerDeclaration),
                null,
            )
        interpreter.execute(conditional)

        // printLn(innerVar); // Should fail - variable not in scope
        val printInner = createPrintLnCall("innerVar")
        val result = interpreter.execute(printInner)

        assertTrue(result.isFailure, "Should not access inner scope variable")
        assertTrue(result.errorOrNull() is UndeclaredVariableError)
    }

    @Test
    fun `should handle assignment to outer scope variable from inner scope`() {
        // let counter: number = 0;
        val declaration = createNumberDeclaration("counter", 0.0)
        interpreter.execute(declaration)

        // if (true) {
        //   counter = 5; // Modify outer variable
        // }
        val assignment = createAssignment("counter", createNumberLiteral(5.0))
        val conditional =
            ConditionalNode(
                createBooleanLiteral(true),
                listOf(assignment),
                null,
            )
        interpreter.execute(conditional)

        // printLn(counter); // Should print "5"
        val printCounter = createPrintLnCall("counter")
        interpreter.execute(printCounter)

        assertEquals(listOf("5"), outputSink.getOutput())
    }

    @Test
    fun `should handle nested if statements with multiple scopes`() {
        // let level: string = "0";
        val level0 = createStringDeclaration("level", "0")
        interpreter.execute(level0)

        // if (true) {
        //   let level: string = "1";
        //   if (true) {
        //     let level: string = "2";
        //     printLn(level); // Should print "2"
        //   }
        //   printLn(level); // Should print "1"
        // }
        val level2Declaration = createStringDeclaration("level", "2")
        val printLevel2 = createPrintLnCall("level")
        val innerIf =
            ConditionalNode(
                createBooleanLiteral(true),
                listOf(level2Declaration, printLevel2),
                null,
            )

        val level1Declaration = createStringDeclaration("level", "1")
        val printLevel1 = createPrintLnCall("level")
        val outerIf =
            ConditionalNode(
                createBooleanLiteral(true),
                listOf(level1Declaration, innerIf, printLevel1),
                null,
            )
        interpreter.execute(outerIf)

        // printLn(level); // Should print "0"
        val printLevel0 = createPrintLnCall("level")
        interpreter.execute(printLevel0)

        assertEquals(listOf("2", "1", "0"), outputSink.getOutput())
    }

    @Test
    fun `should handle variables in else scope`() {
        // let condition: boolean = false;
        val conditionDecl = createBooleanDeclaration("condition", false)
        interpreter.execute(conditionDecl)

        // if (condition) {
        //   let msg: string = "then";
        //   printLn(msg);
        // } else {
        //   let msg: string = "else";
        //   printLn(msg);
        // }
        val thenDecl = createStringDeclaration("msg", "then")
        val thenPrint = createPrintLnCall("msg")
        val elseDecl = createStringDeclaration("msg", "else")
        val elsePrint = createPrintLnCall("msg")

        val conditional =
            ConditionalNode(
                createVariableReference("condition"),
                listOf(thenDecl, thenPrint),
                listOf(elseDecl, elsePrint),
            )
        interpreter.execute(conditional)

        assertEquals(listOf("else"), outputSink.getOutput())
    }

    // Helper methods
    private fun createStringDeclaration(
        name: String,
        value: String,
    ): DeclarationAssignmentNode {
        val identifier = createIdentifier(name)
        val declaration = DeclarationNode(identifier, createToken(TokenType.Keyword.StringType, "string"), true)
        val valueNode = createStringLiteral(value)
        return DeclarationAssignmentNode(declaration, valueNode)
    }

    private fun createNumberDeclaration(
        name: String,
        value: Double,
    ): DeclarationAssignmentNode {
        val identifier = createIdentifier(name)
        val declaration = DeclarationNode(identifier, createToken(TokenType.Keyword.NumberType, "number"), true)
        val valueNode = createNumberLiteral(value)
        return DeclarationAssignmentNode(declaration, valueNode)
    }

    private fun createBooleanDeclaration(
        name: String,
        value: Boolean,
    ): DeclarationAssignmentNode {
        val identifier = createIdentifier(name)
        val declaration = DeclarationNode(identifier, createToken(TokenType.Keyword.BooleanType, "boolean"), true)
        val valueNode = createBooleanLiteral(value)
        return DeclarationAssignmentNode(declaration, valueNode)
    }

    private fun createAssignment(
        name: String,
        value: LiteralNode,
    ): AssignmentNode = AssignmentNode(createIdentifier(name), value)

    private fun createPrintLnCall(variableName: String): FunctionCallNode =
        FunctionCallNode("println", createVariableReference(variableName), true)

    private fun createIdentifier(name: String): IdentifierNode =
        IdentifierNode(
            createToken(TokenType.Identifier, name),
            name,
        )

    private fun createVariableReference(name: String): IdentifierNode = createIdentifier(name)

    private fun createStringLiteral(value: String): LiteralNode =
        LiteralNode(
            createToken(TokenType.StringLiteral, "\"$value\""),
        )

    private fun createNumberLiteral(value: Double): LiteralNode =
        LiteralNode(
            createToken(TokenType.NumberLiteral, value.toString()),
        )

    private fun createBooleanLiteral(value: Boolean): LiteralNode =
        LiteralNode(
            createToken(TokenType.Identifier, value.toString()),
        )

    private fun createToken(
        type: TokenType,
        lexeme: String,
    ): Token = Token(type, lexeme, Location(1, 1, 1))
}
