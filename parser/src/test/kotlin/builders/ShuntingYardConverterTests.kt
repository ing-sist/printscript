package builders

import BinaryOperationNode
import FunctionCallNode
import IdentifierNode
import LiteralNode
import Result
import Token
import builders.expresionHelpers.OperatorPrecedenceManager
import builders.expresionHelpers.ShuntingYardConverter
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import parser.ParseError
import util.tok
import java.util.Queue

class ShuntingYardConverterTests {
    private fun converter() = ShuntingYardConverter(OperatorPrecedenceManager())

    private fun <T> Queue<T>.toListUnsafe(): List<T> =
        ArrayList<T>(this.size).also { out ->
            while (this.isNotEmpty()) out += this.remove()
        }

    // ---------------- Básicos: hojas ----------------

    @Test
    @DisplayName("emite Identifier como nodo hoja")
    fun emitsIdentifierLeaf() {
        val tokens = listOf(tok(TokenType.Identifier, "x"))
        val res = converter().convertToRPNResult(tokens)
        Assertions.assertTrue(res is Result.Success)

        val q = (res as Result.Success).value
        val items = q.toListUnsafe()
        Assertions.assertEquals(1, items.size)
        val id = Assertions.assertInstanceOf(IdentifierNode::class.java, items[0]) as IdentifierNode
        Assertions.assertEquals("x", id.name)
    }

    @Test
    @DisplayName("emite Boolean literal como nodo hoja")
    fun emitsBooleanLeaf() {
        val tokens = listOf(tok(TokenType.BooleanLiteral, "true"))
        val res = converter().convertToRPNResult(tokens)
        Assertions.assertTrue(res is Result.Success)

        val items = (res as Result.Success).value.toListUnsafe()
        val lit = Assertions.assertInstanceOf(LiteralNode::class.java, items[0]) as LiteralNode
        Assertions.assertEquals("true", lit.value.lexeme)
    }

    // ------------- Precedencia y paréntesis -------------

    @Test
    @DisplayName("respeta precedencia y paréntesis: 1 + 2 * (3 + 4) -> RPN [1,2,3,4,+,*,+]")
    fun precedenceAndParentheses() {
        val tokens =
            listOf(
                tok(TokenType.NumberLiteral, "1"),
                tok(TokenType.Operator.Plus, "+"),
                tok(TokenType.NumberLiteral, "2"),
                tok(TokenType.Operator.Multiply, "*"),
                tok(TokenType.LeftParen, "("),
                tok(TokenType.NumberLiteral, "3"),
                tok(TokenType.Operator.Plus, "+"),
                tok(TokenType.NumberLiteral, "4"),
                tok(TokenType.RightParen, ")"),
            )
        val res = converter().convertToRPNResult(tokens)
        Assertions.assertTrue(res is Result.Success)

        val items = (res as Result.Success).value.toListUnsafe()
        // Esperamos: Lit(1), Lit(2), Lit(3), Lit(4), '+', '*', '+'
        Assertions.assertEquals(7, items.size)
        Assertions.assertInstanceOf(LiteralNode::class.java, items[0])
        Assertions.assertInstanceOf(LiteralNode::class.java, items[1])
        Assertions.assertInstanceOf(LiteralNode::class.java, items[2])
        Assertions.assertInstanceOf(LiteralNode::class.java, items[3])
        // Los operadores en la cola final son Tokens
        val plusInner = items[4] as Token // si tu Token real es otro paquete, ajusta el import
        val mul = items[5] as Token
        val plusOuter = items[6] as Token
        Assertions.assertEquals(TokenType.Operator.Plus, plusInner.type)
        Assertions.assertEquals(TokenType.Operator.Multiply, mul.type)
        Assertions.assertEquals(TokenType.Operator.Plus, plusOuter.type)
    }

    @Test
    @DisplayName("RightParen con stack vacío no falla y no hace pop")
    fun strayRightParenDoesNotCrash() {
        val tokens = listOf(tok(TokenType.RightParen, ")"))
        val res = converter().convertToRPNResult(tokens)
        Assertions.assertTrue(res is Result.Success)
        val items = (res as Result.Success).value.toListUnsafe()
        Assertions.assertEquals(0, items.size) // no emitió nada
    }

    // ------------- Funciones: OK -------------

    @Test
    @DisplayName("function call con expresión argumento usa ExpressionBuilder y emite FunctionCallNode")
    fun functionCallWithArgExpression() {
        // f(1 + 2)
        val tokens =
            listOf(
                tok(TokenType.FunctionCall, "f"),
                tok(TokenType.LeftParen, "("),
                tok(TokenType.NumberLiteral, "1"),
                tok(TokenType.Operator.Plus, "+"),
                tok(TokenType.NumberLiteral, "2"),
                tok(TokenType.RightParen, ")"),
            )
        val res = converter().convertToRPNResult(tokens)
        Assertions.assertTrue(res is Result.Success)

        val items = (res as Result.Success).value.toListUnsafe()
        Assertions.assertEquals(1, items.size)
        val fn = Assertions.assertInstanceOf(FunctionCallNode::class.java, items[0]) as FunctionCallNode
        Assertions.assertEquals("f", fn.functionName)
        Assertions.assertFalse(fn.isVoid)

        // el content es un AST de la expresión "1 + 2"
        val arg = fn.content
        val bin = Assertions.assertInstanceOf(BinaryOperationNode::class.java, arg) as BinaryOperationNode
        Assertions.assertEquals(TokenType.Operator.Plus, bin.operator.type)
    }

    @Test
    @DisplayName("function call sin argumentos produce literal de string vacío como content")
    fun functionCallWithoutArgsUsesEmptyStringLiteral() {
        // f()
        val tokens =
            listOf(
                tok(TokenType.FunctionCall, "f"),
                tok(TokenType.LeftParen, "("),
                tok(TokenType.RightParen, ")"),
            )
        val res = converter().convertToRPNResult(tokens)
        Assertions.assertTrue(res is Result.Success)

        val items = (res as Result.Success).value.toListUnsafe()
        val fn = Assertions.assertInstanceOf(FunctionCallNode::class.java, items[0]) as FunctionCallNode
        val arg = Assertions.assertInstanceOf(LiteralNode::class.java, fn.content) as LiteralNode
        Assertions.assertEquals(TokenType.StringLiteral, arg.value.type)
        Assertions.assertEquals("", arg.value.lexeme) // rama argTokens.isEmpty()
    }

    // ------------- Funciones: errores -------------

    @Test
    @DisplayName("function call sin '(' falla con mensaje claro")
    fun functionCallMustBeFollowedByParen() {
        // f IDENT
        val tokens =
            listOf(
                tok(TokenType.FunctionCall, "f"),
                tok(TokenType.Identifier, "x"),
            )
        val res = converter().convertToRPNResult(tokens)
        Assertions.assertTrue(res is Result.Failure)

        val err = (res as Result.Failure).error
        val pe = Assertions.assertInstanceOf(ParseError.InvalidSyntax::class.java, err) as ParseError.InvalidSyntax
        Assertions.assertTrue(pe.message!!.contains("Function call must be followed by '('"))
    }

    @Test
    @DisplayName("println no puede usarse en expresiones")
    fun printlnBannedInExpressions() {
        val tokens =
            listOf(
                tok(TokenType.FunctionCall, "println"),
                tok(TokenType.LeftParen, "("),
                tok(TokenType.StringLiteral, "hi"),
                tok(TokenType.RightParen, ")"),
            )
        val res = converter().convertToRPNResult(tokens)
        Assertions.assertTrue(res is Result.Failure)

        val pe = (res as Result.Failure).error as ParseError.InvalidSyntax
        Assertions.assertTrue(pe.message!!.contains("println cannot be used in expressions"))
    }

    @Test
    @DisplayName("function call sin paréntesis de cierre falla (Unclosed function call)")
    fun unclosedFunctionCallFails() {
        // f(1
        val tokens =
            listOf(
                tok(TokenType.FunctionCall, "f"),
                tok(TokenType.LeftParen, "("),
                tok(TokenType.NumberLiteral, "1"),
                // falta RightParen
            )
        val res = converter().convertToRPNResult(tokens)
        Assertions.assertTrue(res is Result.Failure)

        val pe = (res as Result.Failure).error as ParseError.InvalidSyntax
        Assertions.assertTrue(pe.message!!.contains("Unclosed function call"))
    }

    // ------------- Mezcla: paréntesis + operadores (pop hasta '(' y descartar '(') -------------

    @Test
    @DisplayName("RightParen hace pop de operadores hasta '(' y descarta '('")
    fun rightParenPopsOperatorsThenDiscardsLeftParen() {
        // 1 + (2 + 3)
        val tokens =
            listOf(
                tok(TokenType.NumberLiteral, "1"),
                tok(TokenType.Operator.Plus, "+"),
                tok(TokenType.LeftParen, "("),
                tok(TokenType.NumberLiteral, "2"),
                tok(TokenType.Operator.Plus, "+"),
                tok(TokenType.NumberLiteral, "3"),
                tok(TokenType.RightParen, ")"),
            )
        val res = converter().convertToRPNResult(tokens)
        Assertions.assertTrue(res is Result.Success)

        val items = (res as Result.Success).value.toListUnsafe()
        // Esperado RPN: 1 2 3 + +
        Assertions.assertEquals(5, items.size)
        Assertions.assertInstanceOf(LiteralNode::class.java, items[0]) // 1
        Assertions.assertInstanceOf(LiteralNode::class.java, items[1]) // 2
        Assertions.assertInstanceOf(LiteralNode::class.java, items[2]) // 3
        val innerPlus = items[3] as Token
        val outerPlus = items[4] as Token
        Assertions.assertEquals(TokenType.Operator.Plus, innerPlus.type)
        Assertions.assertEquals(TokenType.Operator.Plus, outerPlus.type)
    }
}
