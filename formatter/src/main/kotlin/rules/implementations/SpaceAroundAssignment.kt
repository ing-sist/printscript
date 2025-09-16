package rules.implementations

import DocBuilder
import Token
import TokenType
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
        if (curr.type is TokenType.Assignment) {
            if (style.spaceAroundAssignment) {
                if (!result.isAtLineStart() && prev.type !is TokenType.Space) {
                    result = result.space()
                }
            }
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
        if (curr.type is TokenType.Assignment && style.spaceAroundAssignment && next.type !is TokenType.Space) {
            result = result.space()
        }
        return result
    }
}
