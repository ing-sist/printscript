package rules.implementations

import DocBuilder
import Token
import TokenType
import config.FormatterStyleConfig

object ColonSpacing : BeforeRule, AfterRule {
    override fun before(
        prev: Token,
        curr: Token,
        next: Token,
        style: FormatterStyleConfig,
        out: DocBuilder,
    ): DocBuilder {
        if (curr.type !is TokenType.Colon) return out

        return if (!style.spaceBeforeColon || prev.type is TokenType.Space) {
            out
        } else {
            if (out.isAtLineStart()) {
                out
            } else {
                out.space()
            }
        }
    }

    override fun after(
        prev: Token,
        curr: Token,
        next: Token,
        style: FormatterStyleConfig,
        out: DocBuilder,
    ): DocBuilder {
        if (curr.type !is TokenType.Colon || !style.spaceAfterColon) return out
        return out.space()
    }
}
