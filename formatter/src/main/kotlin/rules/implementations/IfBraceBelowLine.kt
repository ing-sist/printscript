package rules.implementations

import DocBuilder
import Token
import config.FormatterStyleConfig

object IfBraceBelowLine : BeforeRule {
    override fun before(
        prev: Token,
        curr: Token,
        next: Token,
        style: FormatterStyleConfig,
        out: DocBuilder,
        spaceForbid: SpaceForbid,
    ): DocBuilder {
        if (!style.ifBraceBelowLine) return out
        var result = out
        if (curr.type is TokenType.LeftBrace) {
            result = result.newline()
        }
        return result
    }
}
