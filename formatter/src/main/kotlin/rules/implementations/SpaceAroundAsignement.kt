package rules.implementations

import DocBuilder
import Token
import config.FormatterStyleConfig

object SpaceAroundAsignement : RuleImplementation {
    override fun before(
        tokens: List<Token>,
        index: Int,
        style: FormatterStyleConfig,
        out: DocBuilder,
    ): DocBuilder {
        val token = tokens[index]
        var result = out
        if (token.type is TokenType.Assignment && style.spaceAroundAssignment) {
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
        if (token.type is TokenType.Assignment && style.spaceAroundAssignment) {
            result = result.space()
        }
        return result
    }
}
