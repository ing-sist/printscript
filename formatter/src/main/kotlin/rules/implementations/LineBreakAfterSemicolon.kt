package rules.implementations

import DocBuilder
import Token
import TokenType
import config.FormatterStyleConfig

object LineBreakAfterSemicolon : AfterRule {
    override fun after(
        prev: Token,
        curr: Token,
        next: Token,
        style: FormatterStyleConfig,
        out: DocBuilder,
    ): DocBuilder {
        var result = out
        if (curr.type is TokenType.Semicolon && style.lineBreakAfterSemicolon) {
            if (next.type is TokenType.EOF) return result
            result.newline()
        }

        return result
    }
}
