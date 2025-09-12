import config.StyleConfig
import rules.implementations.RuleImplementation
import rules.implementations.isIfConditionBeforeLeftBrace

object IfStatementIndentation : RuleImplementation {
    private var level: Int = 0

    override fun after(
        tokens: List<Token>,
        index: Int,
        style: StyleConfig,
        out: DocBuilder,
    ): DocBuilder {
        val token = tokens[index]
        val result = out
        if (token.type is TokenType.LeftBrace && isIfConditionBeforeLeftBrace(tokens, index)) {
            level++

            // si {} esta vacio no hago nada
            val next = tokens.getOrNull(index + 1)
            if (next != null && next.type is TokenType.RightBrace) return result

            result.newline() // si tiene algo, agrego newline
            repeat(style.ifStatementIndentation * level) { result.space() }
            // y los esapcios que corresponden
        }

        return result
    }

    override fun before(
        tokens: List<Token>,
        index: Int,
        style: StyleConfig,
        out: DocBuilder,
    ): DocBuilder {
        val token = tokens[index]

        if (token.type is TokenType.RightBrace) {
            // cierro asi que bajo nivel de indentacion
            level = (level - 1).coerceAtLeast(0) // evito que sea neg, si lo es lo pongo en 0

            // si es vacio no hago nada
            val prev = tokens.getOrNull(index - 1)
            if (prev != null && prev.type is TokenType.LeftBrace) {
                return out
            }

            // si no, hago newline y cierro con el nuevo indent level
            out.newline()
            repeat(style.ifStatementIndentation * level) { out.space() }
        }
        return out
    }
}
