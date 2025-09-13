package rules.implementations

import DocBuilder
import Token
import TokenType
import config.FormatterStyleConfig

object KeywordSpacing : RuleImplementation {
    override fun before(
        prev: Token,
        curr: Token,
        next: Token,
        style: FormatterStyleConfig,
        out: DocBuilder,
    ): DocBuilder {
        // agrego siempre un espacio, a menos q este al inicio
        return if (curr.type is TokenType.Keyword && !out.isAtLineStart()) out.space() else out
    }

    override fun after(
        prev: Token,
        curr: Token,
        next: Token,
        style: FormatterStyleConfig,
        out: DocBuilder,
    ): DocBuilder {
        if (curr.type !is TokenType.Keyword) return out

        val needSpaceAfter =
            when (curr.type) {
                is TokenType.Keyword.If -> next.type is TokenType.LeftParen
                is TokenType.Keyword.VariableDeclaration -> next.type is TokenType.Identifier
                else -> false
            }
        return if (needSpaceAfter) out.space() else out
    }
}
