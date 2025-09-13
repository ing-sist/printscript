package rules.implementations

import DocBuilder
import Token
import config.FormatterStyleConfig

object LineBreakAfterSemicolon : RuleImplementation {
    override fun after(
        tokens: List<Token>,
        index: Int,
        style: FormatterStyleConfig,
        out: DocBuilder,
    ): DocBuilder {
        val token = tokens[index]
        var result = out
        if (token.type is TokenType.Semicolon && style.lineBreakAfterSemicolon) {
            result = result.newline()
        }
        return result
    }
}
