package rules.implementations

import DocBuilder
import Token
import TokenType
import config.FormatterStyleConfig

object KeywordSpacing : RuleImplementation {
    override fun before(
        tokens: List<Token>,
        index: Int,
        style: FormatterStyleConfig,
        out: DocBuilder,
    ): DocBuilder {
        val curr = tokens[index].type
        // agrego siempre un espacio, a menos q este al inicio
        return if (curr is TokenType.Keyword && !out.isAtLineStart()) out.space() else out
    }

    override fun after(
        tokens: List<Token>,
        index: Int,
        style: FormatterStyleConfig,
        out: DocBuilder,
    ): DocBuilder {
        val curr = tokens[index].type

        if (tokens.getOrNull(index + 1)?.type == null || curr !is TokenType.Keyword) return out

        val next = tokens.getOrNull(index + 1)?.type

        val needSpaceAfter =
            when (curr) {
                is TokenType.Keyword.If -> next is TokenType.LeftParen
                is TokenType.Keyword.VariableDeclaration -> next is TokenType.Identifier
                else -> false
            }
        return if (needSpaceAfter) out.space() else out
    }
}
