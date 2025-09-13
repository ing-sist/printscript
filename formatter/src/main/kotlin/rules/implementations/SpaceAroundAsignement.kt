package rules.implementations

import DocBuilder
import Token
import config.FormatterStyleConfig

object SpaceAroundAsignement : RuleImplementation {
    override fun before(
        prev: Token,
        curr: Token,
        next: Token,
        style: FormatterStyleConfig,
        out: DocBuilder,
    ): DocBuilder {
        var result = out
        if (curr.type is TokenType.Assignment && style.spaceAroundAssignment) {
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
        if (curr.type is TokenType.Assignment && style.spaceAroundAssignment) {
            result = result.space()
        }
        return result
    }
}
