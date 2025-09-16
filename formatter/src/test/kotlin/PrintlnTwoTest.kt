import config.FormatterStyleConfig
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class PrintlnTwoTest {
    private fun t(
        type: TokenType,
        lex: String,
    ) = Token(type, lex, Location(1, 1, 1))

    @Test
    fun `deja dos line breaks despues de cada println segun config`() {
        // Input tokens:
        // let something:string = "a really cool thing";
        // println(something);
        // println("in the way she moves");
        val tokens =
            listOf(
                t(TokenType.Keyword.VariableDeclaration, "let"),
                t(TokenType.Space, " "),
                t(TokenType.Identifier, "something"),
                t(TokenType.Colon, ":"),
                t(TokenType.StringType, "string"),
                t(TokenType.Space, " "),
                t(TokenType.Assignment, "="),
                t(TokenType.Space, " "),
                t(TokenType.StringLiteral, "\"a really cool thing\""),
                t(TokenType.Semicolon, ";"),
                t(TokenType.Newline, "\n"),
                t(TokenType.Identifier, "println"),
                t(TokenType.LeftParen, "("),
                t(TokenType.Identifier, "something"),
                t(TokenType.RightParen, ")"),
                t(TokenType.Semicolon, ";"),
                t(TokenType.Newline, "\n"),
                t(TokenType.Identifier, "println"),
                t(TokenType.LeftParen, "("),
                t(TokenType.StringLiteral, "\"in the way she moves\""),
                t(TokenType.RightParen, ")"),
                t(TokenType.Semicolon, ";"),
            )

        val stream = MockTokenStream(tokens)
        val fmt = Formatter(FormatterRuleImplementations.IMPLEMENTATIONS)

        // Cargamos estilo desde JSON (clave pedida)
        val style =
            FormatterStyleConfig.fromJson(
                """
                {
                  "line-breaks-after-println": 2
                }
                """.trimIndent(),
            )

        val out = fmt.format(stream, style, DocBuilder.inMemory())

        val expected =
            """
            let something:string = "a really cool thing";


            println(something);


            println("in the way she moves");
            """.trimIndent()

        assertEquals(expected, out.build())
    }
}
