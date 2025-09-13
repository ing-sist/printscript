package rules.implementations

import DocBuilder
import Token
import TokenType
import config.FormatterStyleConfig

object CommaSpacing : RuleImplementation {
    override fun before(
        prev: Token,
        curr: Token,
        next: Token,
        style: FormatterStyleConfig,
        out: DocBuilder,
    ): DocBuilder {
        var result = out
        if (curr.type is TokenType.Colon && style.spaceBeforeColon) {
            result = result.space()
        }
        return result
    }

    override fun after(
        prev: Token,
        curr: Token,
        next: Token,
        style: FormatterStyleConfig,
        out: DocBuilder,
    ): DocBuilder {
        var result = out
        if (curr.type is TokenType.Comma) {
            result = result.space()
        }
        return result
    }
}
