package rules.implementations

import DocBuilder
import Token
import TokenType
import config.FormatterStyleConfig

object KeywordSpacing : BeforeRule, AfterRule {
    override fun before(
        prev: Token,
        curr: Token,
        next: Token,
        style: FormatterStyleConfig,
        out: DocBuilder,
    ): DocBuilder {
        var newOut = out
        if (curr.type !is TokenType.Keyword.VariableDeclaration) return newOut
        if (!out.isAtLineStart() ||
            prev.type !is TokenType.Space ||
            out.getLastSent().toString() != " "
        ) {
            newOut = out.space()
        }
        return newOut
    }

    override fun after(
        prev: Token,
        curr: Token,
        next: Token,
        style: FormatterStyleConfig,
        out: DocBuilder,
    ): DocBuilder {
        val newOut = out
        if (curr.type !is TokenType.Keyword.VariableDeclaration ||
            next.type is TokenType.Space
        ) {
            return newOut
        }

        return newOut.space()
    }
}
