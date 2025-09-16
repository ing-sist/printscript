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
        var out = out
        if (curr.type is TokenType.Keyword && !out.isAtLineStart() && prev.type !is TokenType.Space) {
            out = out.space()
        }
        return out
    }

    override fun after(
        prev: Token,
        curr: Token,
        next: Token,
        style: FormatterStyleConfig,
        out: DocBuilder,
    ): DocBuilder {
        val out = out
        if (curr.type !is TokenType.Keyword || next.type is TokenType.Space) return out

        return out.space()
    }
}
