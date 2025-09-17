package rules.implementations

import DocBuilder
import Token
import config.FormatterStyleConfig

object SpaceBetweenTokens : AfterRule {
    override fun after(
        prev: Token,
        curr: Token,
        next: Token,
        style: FormatterStyleConfig,
        out: DocBuilder,
    ): DocBuilder {
        var newOut = out
        if (!style.spaceBetweenTokens) return newOut
        if ((
                curr.type !is TokenType.Space &&
                    out.getLastSent().toString() != " " &&
                    !out.isAtLineStart()
            )
        ) {
            newOut = newOut.space()
        }
        return newOut
    }
}
