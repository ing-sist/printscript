package rules.implementations

import DocBuilder
import Token
import config.FormatterStyleConfig

object LineBreakAfterSemicolon : RuleImplementation {
    override fun after(
        prev: Token,
        curr: Token,
        next: Token,
        style: FormatterStyleConfig,
        out: DocBuilder,
    ): DocBuilder {
        var result = out
        if (curr.type is TokenType.Semicolon && style.lineBreakAfterSemicolon) {
            result = result.newline()
        }
        return result
    }
}
