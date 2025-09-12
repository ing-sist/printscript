import config.StyleConfig
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import rules.implementations.RuleImplementation
import java.io.File

class FormatterTest {
    private fun format(source: String): String {
        // 1) cargar estilo desde JSON (tal cual tu setup)
        val cfgPath = File("src/test/kotlin/config1.json")
        val style = StyleConfig.fromPath(cfgPath)

        // 2) construir el lexer normal (factory real de tu proyecto)
        val lexer = LexerGenerator.createDefaultLexer()

        // 3) lexear
        val tokens =
            when (val res = lexer.lex(source.trim())) {
                is Result.Success -> res.value
                is Result.Failure -> error("Lexing failed: ${res.error}")
            }

        // 4) reglas del formatter (mismo orden)
        val rules: List<RuleImplementation> =
            listOf(
                InlineBraceIfStatement,
                IfStatementIndentation,
                SpaceBeforeColon,
                SpaceAfterColon,
                SpaceAroundAsignement,
                SpaceAroundOperators,
                SpaceBetweenTokens,
                LineBreakBeforePrintln,
                LineBreakAfterSemicolon,
            )

        // 5) formatear
        return Formatter(rules)
            .format(tokens, style, DocBuilder())
            .build()
    }

    @Test
    fun `println inserta N saltos y luego continua`() {
        val out = format("""println( "a" );x=1;""")
        val expected =
            """
            
            
            println("a");
            x = 1;
            """.trimIndent()
        assertEquals(expected, out.trimEnd())
    }

    @Test
    fun `declaracion tipada - espacios antes y despues de colon`() {
        val out = format("""let   x:number  ;""")
        val expected = "let x: number;"
        assertEquals(expected, out.trimEnd())
    }

    @Test
    fun `asignacion - espacios alrededor de igual`() {
        val out = format("""x=1;""")
        val expected = "x = 1;"
        assertEquals(expected, out.trimEnd())
    }

    @Test
    fun `operadores aritmeticos - espacios alrededor y dentro de parentesis`() {
        val out = format("""a+1*(b-2)/c;""")
        val expected = "a + 1 * (b - 2) / c;"
        assertEquals(expected, out.trimEnd())
    }

    @Test
    fun `salto de linea despues de punto y coma separa sentencias`() {
        val out = format("""x=1; y=2; z=3;""")
        val expected =
            """
            x = 1;
            y = 2;
            z = 3;
            """.trimIndent()
        assertEquals(expected, out.trimEnd())
    }

    @Test
    fun `no hay espacio antes de separadores y si entre argumentos`() {
        val out = format("""f(a,b,c);""")
        val expected = "f(a, b, c);"
        assertEquals(expected, out.trimEnd())
    }

    @Test
    fun `colapso de espacios multiples entre tokens`() {
        val out = format("""let    y    :    number     ;""")
        val expected = "let y: number;"
        assertEquals(expected, out.trimEnd())
    }

    @Test
    fun `sin espacio fantasma al final`() {
        val out = format("""x=1;""")
        val trimmed = out.trimEnd()
        val expected = "x = 1;"
        assertEquals(expected.last(), trimmed.last(), "El último carácter debe ser ';' sin espacio extra")
        assertEquals(expected, trimmed)
    }

    @Test
    fun `println consecutivos respetan N saltos entre ellos`() {
        val out = format("""println("a");println("b");x=1;""")
        val expected =
            """
            
            
            println("a");
            
            
            println("b");
            x = 1;
            """.trimIndent()
        assertEquals(expected, out.trimEnd())
    }

    @Test
    fun `mixto - declaracion, println y asignacion`() {
        val out = format("""let   y: number ; println("ok");y=y+1;""")
        val expected =
            """
            let y: number;
            
            
            println("ok");
            y = y + 1;
            """.trimIndent()
        assertEquals(expected, out.trimEnd())
    }
}
