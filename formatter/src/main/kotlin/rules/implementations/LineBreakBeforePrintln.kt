package rules.implementations

import DocBuilder
import Token
import config.FormatterStyleConfig

object LineBreakBeforePrintln : BeforeRule {
    override fun before(
        prev: Token,
        curr: Token,
        next: Token,
        style: FormatterStyleConfig,
        out: DocBuilder,
    ): DocBuilder {
        if (style.lineBreakBeforePrintln == 0) return out
        var result = out
        if (curr.lexeme.lowercase() == "println") {
            repeat(style.lineBreakBeforePrintln) {
                result = result.newline()
            }
        }
        return result
    }
}
