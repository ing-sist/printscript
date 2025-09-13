package rules.implementations

import DocBuilder
import Token
import config.FormatterStyleConfig

object LineBreakBeforePrintln : RuleImplementation {
    override fun before(
        tokens: List<Token>,
        index: Int,
        style: FormatterStyleConfig,
        out: DocBuilder,
    ): DocBuilder {
        val token = tokens[index]
        var result = out
        if (token.lexeme.lowercase() == "println") {
            repeat(style.lineBreakBeforePrintln) {
                result = result.newline()
            }
        }
        return result
    }
}
