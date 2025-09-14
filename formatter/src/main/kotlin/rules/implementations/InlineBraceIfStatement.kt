import config.FormatterStyleConfig
import rules.implementations.BeforeRule

object InlineBraceIfStatement : BeforeRule {
    override fun before(
        prev: Token,
        curr: Token,
        next: Token,
        style: FormatterStyleConfig,
        out: DocBuilder,
    ): DocBuilder {
        if (!style.inlineIfBraceIfStatement) return out
        var result = out
        if (curr.type is TokenType.LeftBrace) result = result.space()
        return result
    }
}
