package rules.implementations

import DocBuilder
import Token
import TokenType
import config.FormatterStyleConfig

object CommaSpacing : RuleImplementation {
    override fun before(
        tokens: List<Token>,
        index: Int,
        style: FormatterStyleConfig,
        out: DocBuilder,
    ): DocBuilder {
        val token = tokens[index]
        var result = out
        if (token.type is TokenType.Colon && style.spaceBeforeColon) {
            result = result.space()
        }
        return result
    }

    override fun after(
        tokens: List<Token>,
        index: Int,
        style: FormatterStyleConfig,
        out: DocBuilder,
    ): DocBuilder {
        val token = tokens[index]
        var result = out
        if (token.type is TokenType.Comma) {
            result = result.space()
        }
        return result
    }
}
