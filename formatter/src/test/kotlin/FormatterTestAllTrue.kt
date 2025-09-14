import config.FormatterStyleConfig
import config.RuleImplementations
import org.junit.jupiter.api.Test
import rules.implementations.RuleImplementation
import kotlin.test.assertEquals

class FormatterTestAllTrue {
    private fun format1(source: String): String {
        val style = FormatterStyleConfig.fromPath("src/test/kotlin/config1.json")
        val lexer = LexerGenerator.createDefaultLexer()

        val stream = streamFromSource(lexer, source)

        val rules: List<RuleImplementation> = RuleImplementations.IMPLEMENTATIONS

        return Formatter(rules)
            .format(stream, style, DocBuilder.inMemory())
            .build()
    }

    private fun streamFromSource(
        lexer: Lexer,
        source: String,
    ): TokenStream {
        val tokens =
            when (val res = lexer.lex(source.trim())) {
                is Result.Success -> res.value
                is Result.Failure -> error("Lexing failed: ${res.error}")
            }
        return TokenStream(tokens)
    }

    @Test
    fun `println inserta N saltos y luego continua`() {
        val out = format1("""println( "a" );x=1;""")
        val expected =
            """
            
            println("a");
            x = 1;
            """.trimIndent()
        assertEquals(expected, out.trimEnd())
    }

    @Test
    fun `declaracion tipada - espacios antes y despues de colon`() {
        val out = format1("""let   x:number  ;""")
        val expected = "let x : number;"
        assertEquals(expected, out.trimEnd())
    }

    @Test
    fun `asignacion - espacios alrededor de igual`() {
        val out = format1("""x=1;""")
        val expected = "x = 1;"
        assertEquals(expected, out.trimEnd())
    }

    @Test
    fun `operadores aritmeticos - espacios alrededor y dentro de parentesis`() {
        val out = format1("""a+1*(b-2)/c;""")
        val expected = "a + 1 * (b - 2) / c;"
        assertEquals(expected, out.trimEnd())
    }

    @Test
    fun `salto de linea despues de punto y coma separa sentencias`() {
        val out = format1("""x=1; y=2; z=3;""")
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
        val out = format1("""f(a,b,c);""")
        val expected = "f(a, b, c);"
        assertEquals(expected, out.trimEnd())
    }

    @Test
    fun `colapso de espacios multiples entre tokens`() {
        val out = format1("""let    y    :    number     ;""")
        val expected = "let y : number;"
        assertEquals(expected, out.trimEnd())
    }

    @Test
    fun `sin espacio fantasma al final`() {
        val out = format1("""x=1;""")
        val trimmed = out.trimEnd()
        val expected = "x = 1;"
        assertEquals(expected.last(), trimmed.last(), "El último carácter debe ser ';' sin espacio extra")
        assertEquals(expected, trimmed)
    }

    @Test
    fun `println consecutivos respetan N saltos entre ellos`() {
        val out = format1("""println("a");println("b");x=1;""")
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
        val out = format1("""let   y: number ; println("ok");y=y+1;""")
        val expected =
            """
            let y : number;
            
            println("ok");
            y = y + 1;
            """.trimIndent()
        assertEquals(expected, out.trimEnd())
    }

    @Test
    fun `brace inline - mismo renglón que el if`() {
        val out =
            format1(
                """
                if (x > 0){println("ok");}
                """.trimIndent(),
            )

        val expected =
            """
            if (x > 0) {
            
                println("ok");
            }
            """.trimIndent()

        assertEquals(expected, out.trimEnd())
    }

    @Test
    fun `indentación simple dentro del if`() {
        val out =
            format1(
                """
                if (x > 0) {x=1;}
                """.trimIndent(),
            )

        val expected =
            """
            if (x > 0) {
                x = 1;
            }
            """.trimIndent()

        assertEquals(expected, out.trimEnd())
    }

    @Test
    fun `indentación configurable - 4 espacios`() {
        val out =
            format1(
                """
                if (x > 0) {println("a");}
                """.trimIndent(),
            )

        val expected =
            """
            if (x > 0) {
            
                println("a");
            }
            """.trimIndent()

        assertEquals(expected, out.trimEnd())
    }

    @Test
    fun `if-else anidado respeta indentacion y alineacion de llaves`() {
        val out =
            format1(
                """
                if (a){if(b){x=1;}else{y=2;}}
                """.trimIndent(),
            )

        val expected =
            """
            if (a) {
                if (b) {
                    x = 1;
                } else {
                    y = 2;
                }
            }
            """.trimIndent()
        assertEquals(expected, out.trimEnd())
    }

    @Test
    fun `if con muchas sentencias separadas por punto y coma`() {
        val out =
            format1(
                """
                 if (x > 0) {
                    a = 1;b = 2;c = 3;
                    d = 4;
                }
                """.trimIndent(),
            )

        val expected =
            """
            if (x > 0) {
                a = 1;
                b = 2;
                c = 3;
                d = 4;
            }
            """.trimIndent()

        assertEquals(expected, out.trimEnd())
    }
}
