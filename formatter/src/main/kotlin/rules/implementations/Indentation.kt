import config.FormatterStyleConfig
import rules.implementations.RuleImplementation

object Indentation : RuleImplementation {
    private fun indent(
        doc: DocBuilder,
        spaces: Int,
    ): DocBuilder {
        var d = doc
        repeat(spaces) { d = d.space() }
        return d
    }

    override fun before(
        tokens: List<Token>,
        index: Int,
        style: FormatterStyleConfig,
        out: DocBuilder,
    ): DocBuilder {
        var result = out
        val curr = tokens[index].type

        if (curr is TokenType.RightBrace) {
            // si tengo un }, bajo indent level
            val levelBefore = indentLevelUpTo(tokens, index).coerceAtLeast(0)

            val prev = tokens.getOrNull(index - 1)?.type
            if (prev !is TokenType.Semicolon) {
                result = result.newline()
            }
            result = indent(result, levelBefore * style.indentation)
        }
        return result
    }

    override fun after(
        tokens: List<Token>,
        index: Int,
        style: FormatterStyleConfig,
        out: DocBuilder,
    ): DocBuilder {
        var result = out
        val curr = tokens[index].type
        val next = tokens.getOrNull(index + 1)?.type

        when (curr) {
            is TokenType.LeftBrace -> {
                // encuento un {, entonces hago newline y subo el level de indent
                val levelInside = indentLevelUpTo(tokens, index)
                result = result.newline()
                result = indent(result, levelInside * style.indentation)
            }

            is TokenType.Semicolon -> {
                // tengo que poner indentacion porque sigue el bloque
                if (next !is TokenType.RightBrace) { // no pongo si cierro el bloque
                    val levelNow = indentLevelUpTo(tokens, index)
                    result = indent(result, levelNow * style.indentation)
                }
            }
            else -> {}
        }
        return result
    }
}

fun indentLevelUpTo(
    tokens: List<Token>,
    end: Int,
): Int {
    var level = 0
    var i = 0
    while (i <= end) {
        when (tokens[i].type) {
            is TokenType.LeftBrace -> level++
            is TokenType.RightBrace -> level = (level - 1).coerceAtLeast(0)
            else -> {}
        }
        i++
    }
    return level
}
