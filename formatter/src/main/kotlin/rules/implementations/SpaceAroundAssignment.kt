package rules.implementations

import DocBuilder
import Token
import config.FormatterStyleConfig

object SpaceAroundAssignment : BeforeRule, AfterRule {
    override fun before(
        prev: Token,
        curr: Token,
        next: Token,
        style: FormatterStyleConfig,
        out: DocBuilder,
    ): DocBuilder {
        var result = out
        if (curr.type is TokenType.Assignment && style.spaceAroundAssignment && prev.type !is TokenType.Space) {
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
        if (curr.type is TokenType.Assignment && style.spaceAroundAssignment && prev.type !is TokenType.Space) {
            result = result.space()
        }
        return result
    }
}
