import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import kotlin.test.DefaultAsserter.fail
import kotlin.test.Test
import kotlin.test.assertEquals

class LexerTest {
    private lateinit var lexer: Lexer

    @BeforeEach
    fun setup() {
        // Crear lexer con todas las reglas necesarias para PrintScript 1.0
        lexer =
            LexerGenerator.createLexer(
                linkedMapOf(
                    // Keywords
                    "\\blet\\b" to TokenType.VariableDeclaration,
                    "\\bprintln\\b" to TokenType.FunctionCall,
                    // Data types
                    "\\bstring\\b" to TokenType.StringType,
                    "\\bnumber\\b" to TokenType.NumberType,
                    // String Literals
                    "\"([^\"\\\\]|\\\\.)*\"" to TokenType.StringLiteral,
                    "'([^'\\\\]|\\\\.)*'" to TokenType.StringLiteral,
                    // Number Literals
                    "\\d+\\.\\d+" to TokenType.NumberLiteral,
                    "\\d+" to TokenType.NumberLiteral,
                    // Operators
                    "=" to TokenType.Assignment,
                    "\\+" to TokenType.Plus,
                    "-" to TokenType.Minus,
                    "\\*" to TokenType.Multiply,
                    "/" to TokenType.Divide,
                    // Symbols
                    ":" to TokenType.Colon,
                    ";" to TokenType.Semicolon,
                    "\\(" to TokenType.LeftParen,
                    "\\)" to TokenType.RightParen,
                    // Identifiers (must be last)
                    "[a-zA-Z_][a-zA-Z0-9_]*" to TokenType.Identifier,
                ),
            )
    }

    private fun getTokens(input: String): List<Token> {
        val result = lexer.lex(input)
        return result.fold<List<Token>>(
            { tokens -> tokens },
            { error -> fail("Error al procesar '$input': $error") },
        )
    }

    @Test
    fun specialKeywordTest() {
        val tokens = getTokens("let")

        assertEquals(TokenType.VariableDeclaration, tokens.first().type)
        assertEquals(TokenType.EOF, tokens.last().type)
    }

    @Test
    fun identifierTest() {
        val tokens = getTokens("miVariable")

        val first = tokens.first()
        assertEquals(TokenType.Identifier, first.type)
        assertEquals("miVariable", first.lexeme)
    }

    // Test 1: Declaración de variable completa con tipo y asignación
    @Test
    fun completeVariableDeclarationTest() {
        val input =
            """
            let x: number = 42;
            let message: string = "Hello World";
            """.trimIndent()

        val tokens = getTokens(input)

        // Primera línea: let x: number = 42;
        assertEquals(TokenType.VariableDeclaration, tokens[0].type) // let
        assertEquals(TokenType.Identifier, tokens[1].type) // x
        assertEquals(TokenType.Colon, tokens[2].type) // :
        assertEquals(TokenType.NumberType, tokens[3].type) // number
        assertEquals(TokenType.Assignment, tokens[4].type) // =
        assertEquals(TokenType.NumberLiteral, tokens[5].type) // 42
        assertEquals(TokenType.Semicolon, tokens[6].type) // ;

        // Segunda línea: let message: string = "Hello World";
        assertEquals(TokenType.VariableDeclaration, tokens[7].type) // let
        assertEquals(TokenType.Identifier, tokens[8].type) // message
        assertEquals(TokenType.Colon, tokens[9].type) // :
        assertEquals(TokenType.StringType, tokens[10].type) // string
        assertEquals(TokenType.Assignment, tokens[11].type) // =
        assertEquals(TokenType.StringLiteral, tokens[12].type) // "Hello World"
        assertEquals(TokenType.Semicolon, tokens[13].type) // ;
        assertEquals(TokenType.EOF, tokens[14].type) // EOF

        // Verificar valores específicos
        assertEquals("x", tokens[1].lexeme)
        assertEquals("42", tokens[5].lexeme)
        assertEquals("message", tokens[8].lexeme)
        assertEquals("\"Hello World\"", tokens[12].lexeme)
    }

    // Test 2: Operaciones aritméticas complejas
    @Test
    fun arithmeticOperationsTest() {
        val input =
            """
            let result: number = 10 + 5 * 2 - 3;
            let calculation: number = result / 4;
            """.trimIndent()

        val tokens = getTokens(input)

        // Primera línea: let result: number = 10 + 5 * 2 - 3;
        assertEquals(TokenType.VariableDeclaration, tokens[0].type) // let
        assertEquals(TokenType.Identifier, tokens[1].type) // result
        assertEquals(TokenType.Colon, tokens[2].type) // :
        assertEquals(TokenType.NumberType, tokens[3].type) // number
        assertEquals(TokenType.Assignment, tokens[4].type) // =
        assertEquals(TokenType.NumberLiteral, tokens[5].type) // 10
        assertEquals(TokenType.Plus, tokens[6].type) // +
        assertEquals(TokenType.NumberLiteral, tokens[7].type) // 5
        assertEquals(TokenType.Multiply, tokens[8].type) // *
        assertEquals(TokenType.NumberLiteral, tokens[9].type) // 2
        assertEquals(TokenType.Minus, tokens[10].type) // -
        assertEquals(TokenType.NumberLiteral, tokens[11].type) // 3
        assertEquals(TokenType.Semicolon, tokens[12].type) // ;

        // Segunda línea: let calculation: number = result / 4;
        assertEquals(TokenType.VariableDeclaration, tokens[13].type) // let
        assertEquals(TokenType.Identifier, tokens[14].type) // calculation
        assertEquals(TokenType.Colon, tokens[15].type) // :
        assertEquals(TokenType.NumberType, tokens[16].type) // number
        assertEquals(TokenType.Assignment, tokens[17].type) // =
        assertEquals(TokenType.Identifier, tokens[18].type) // result
        assertEquals(TokenType.Divide, tokens[19].type) // /
        assertEquals(TokenType.NumberLiteral, tokens[20].type) // 4
        assertEquals(TokenType.Semicolon, tokens[21].type) // ;
        assertEquals(TokenType.EOF, tokens[22].type) // EOF
    }

    // Test 3: Múltiples llamadas a println con diferentes tipos
    @Test
    fun printlnCallsTest() {
        val input =
            """
            println("Starting program");
            println(42);
            println(3.14);
            println(myVariable);
            """.trimIndent()

        val tokens = getTokens(input)

        // Primera línea: println("Starting program");
        assertEquals(TokenType.FunctionCall, tokens[0].type) // println
        assertEquals(TokenType.LeftParen, tokens[1].type) // (
        assertEquals(TokenType.StringLiteral, tokens[2].type) // "Starting program"
        assertEquals(TokenType.RightParen, tokens[3].type) // )
        assertEquals(TokenType.Semicolon, tokens[4].type) // ;

        // Segunda línea: println(42);
        assertEquals(TokenType.FunctionCall, tokens[5].type) // println
        assertEquals(TokenType.LeftParen, tokens[6].type) // (
        assertEquals(TokenType.NumberLiteral, tokens[7].type) // 42
        assertEquals(TokenType.RightParen, tokens[8].type) // )
        assertEquals(TokenType.Semicolon, tokens[9].type) // ;

        // Tercera línea: println(3.14);
        assertEquals(TokenType.FunctionCall, tokens[10].type) // println
        assertEquals(TokenType.LeftParen, tokens[11].type) // (
        assertEquals(TokenType.NumberLiteral, tokens[12].type) // 3.14
        assertEquals(TokenType.RightParen, tokens[13].type) // )
        assertEquals(TokenType.Semicolon, tokens[14].type) // ;

        // Cuarta línea: println(myVariable);
        assertEquals(TokenType.FunctionCall, tokens[15].type) // println
        assertEquals(TokenType.LeftParen, tokens[16].type) // (
        assertEquals(TokenType.Identifier, tokens[17].type) // myVariable
        assertEquals(TokenType.RightParen, tokens[18].type) // )
        assertEquals(TokenType.Semicolon, tokens[19].type) // ;
        assertEquals(TokenType.EOF, tokens[20].type) // EOF

        // Verificar valores específicos
        assertEquals("\"Starting program\"", tokens[2].lexeme)
        assertEquals("42", tokens[7].lexeme)
        assertEquals("3.14", tokens[12].lexeme)
        assertEquals("myVariable", tokens[17].lexeme)
    }

    // Test 4: Concatenación de strings y números
    @Test
    fun stringConcatenationTest() {
        val input =
            """
            let name: string = "John";
            let age: number = 25;
            let greeting: string = "Hello " + name + ", you are " + age + " years old";
            """.trimIndent()

        val tokens = getTokens(input)

        // Primera línea: let name: string = "John";
        assertEquals(TokenType.VariableDeclaration, tokens[0].type) // let
        assertEquals(TokenType.Identifier, tokens[1].type) // name
        assertEquals(TokenType.Colon, tokens[2].type) // :
        assertEquals(TokenType.StringType, tokens[3].type) // string
        assertEquals(TokenType.Assignment, tokens[4].type) // =
        assertEquals(TokenType.StringLiteral, tokens[5].type) // "John"
        assertEquals(TokenType.Semicolon, tokens[6].type) // ;

        // Tercera línea (expresión compleja): let greeting: string = "Hello " + name + ", you are " + age + " years old";
        val greetingStartIndex = 14 // Después de la segunda declaración
        assertEquals(TokenType.VariableDeclaration, tokens[greetingStartIndex].type) // let
        assertEquals(TokenType.Identifier, tokens[greetingStartIndex + 1].type) // greeting
        assertEquals(TokenType.Colon, tokens[greetingStartIndex + 2].type) // :
        assertEquals(TokenType.StringType, tokens[greetingStartIndex + 3].type) // string
        assertEquals(TokenType.Assignment, tokens[greetingStartIndex + 4].type) // =
        assertEquals(TokenType.StringLiteral, tokens[greetingStartIndex + 5].type) // "Hello "
        assertEquals(TokenType.Plus, tokens[greetingStartIndex + 6].type) // +
        assertEquals(TokenType.Identifier, tokens[greetingStartIndex + 7].type) // name
        assertEquals(TokenType.Plus, tokens[greetingStartIndex + 8].type) // +
        assertEquals(TokenType.StringLiteral, tokens[greetingStartIndex + 9].type) // ", you are "
        assertEquals(TokenType.Plus, tokens[greetingStartIndex + 10].type) // +
        assertEquals(TokenType.Identifier, tokens[greetingStartIndex + 11].type) // age
        assertEquals(TokenType.Plus, tokens[greetingStartIndex + 12].type) // +
        assertEquals(TokenType.StringLiteral, tokens[greetingStartIndex + 13].type) // " years old"
        assertEquals(TokenType.Semicolon, tokens[greetingStartIndex + 14].type) // ;

        // Verificar valores específicos de strings
        assertEquals("\"John\"", tokens[5].lexeme)
        assertEquals("\"Hello \"", tokens[greetingStartIndex + 5].lexeme)
        assertEquals("\", you are \"", tokens[greetingStartIndex + 9].lexeme)
        assertEquals("\" years old\"", tokens[greetingStartIndex + 13].lexeme)
    }

    // Test 5: Programa completo de PrintScript 1.0
    @Test
    fun completePrintScriptProgramTest() {
        val input =
            """
            let x: number = 5;
            let y: number = 10;
            let sum: number = x + y;
            let product: number = x * y;
            let message: string = "Results:";
            
            println(message);
            println("Sum: " + sum);
            println("Product: " + product);
            println("Average: " + sum / 2);
            """.trimIndent()

        val tokens = getTokens(input)

        // Verificar que tenemos el número correcto de tokens (sin contar espacios)
        val nonEofTokens = tokens.filter { it.type != TokenType.EOF }
        assertTrue(nonEofTokens.size > 50, "El programa debería generar más de 50 tokens")

        // Verificar que termina con EOF
        assertEquals(TokenType.EOF, tokens.last().type)

        // Verificar algunas declaraciones clave
        val letIndices =
            tokens.mapIndexedNotNull { index, token ->
                if (token.type == TokenType.VariableDeclaration) index else null
            }
        assertEquals(5, letIndices.size, "Debería haber 5 declaraciones 'let'")

        // Verificar llamadas a println
        val printlnIndices =
            tokens.mapIndexedNotNull { index, token ->
                if (token.type == TokenType.FunctionCall && token.lexeme == "println") index else null
            }
        assertEquals(4, printlnIndices.size, "Debería haber 4 llamadas a 'println'")

        // Verificar que todos los statements terminan con punto y coma
        val semicolonIndices =
            tokens.mapIndexedNotNull { index, token ->
                if (token.type == TokenType.Semicolon) index else null
            }
        assertEquals(9, semicolonIndices.size, "Debería haber 9 puntos y coma")

        // Verificar operadores aritméticos
        val plusCount = tokens.count { it.type == TokenType.Plus }
        val multiplyCount = tokens.count { it.type == TokenType.Multiply }
        val divideCount = tokens.count { it.type == TokenType.Divide }

        assertTrue(plusCount >= 3, "Debería haber al menos 3 operadores '+' (suma y concatenación)")
        assertTrue(multiplyCount >= 1, "Debería haber al menos 1 operador '*'")
        assertTrue(divideCount >= 1, "Debería haber al menos 1 operador '/'")

        // Verificar ubicaciones de tokens (que tienen line, startCol, endCol)
        tokens.forEach { token ->
            assertTrue(token.location.line >= 1, "La línea debe ser >= 1")
            assertTrue(token.location.startCol >= 1, "La columna de inicio debe ser >= 1")
            assertTrue(token.location.endCol >= token.location.startCol, "La columna final debe ser >= columna inicial")
        }
    }
}
