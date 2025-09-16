package rules.implementations

import DocBuilder
import Token
import config.FormatterStyleConfig

object ColonSpacing : BeforeRule, AfterRule {
    override fun before(
        prev: Token,
        curr: Token,
        next: Token,
        style: FormatterStyleConfig,
        out: DocBuilder,
        spaceForbid: SpaceForbid,
    ): DocBuilder {
        if (curr.type is TokenType.Colon) {
            return if (!style.spaceBeforeColon) {
                spaceForbid.forbidBefore()
                out
            } else {
                val r = if (!out.isAtLineStart()) out.space() else out
                spaceForbid.forbidBefore()
                r
            }
        }
        return out
    }

    override fun after(
        prev: Token,
        curr: Token,
        next: Token,
        style: FormatterStyleConfig,
        out: DocBuilder,
        spaceForbid: SpaceForbid,
    ): DocBuilder {
        var result = out
        if (curr.type is TokenType.Colon && style.spaceAfterColon && next.type !is TokenType.Space) {
            result = result.space()
            spaceForbid.forbidAfter()
        }
        if (curr.type is TokenType.Colon && !style.spaceBeforeColon) {
            spaceForbid.forbidAfter()
        }

        return result
    }
}
