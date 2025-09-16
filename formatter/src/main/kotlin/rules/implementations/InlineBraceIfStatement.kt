import config.FormatterStyleConfig
import rules.implementations.BeforeRule
import rules.implementations.SpaceForbid

object InlineBraceIfStatement : BeforeRule {
    override fun before(
        prev: Token,
        curr: Token,
        next: Token,
        style: FormatterStyleConfig,
        out: DocBuilder,
        spaceForbid: SpaceForbid,
    ): DocBuilder {
        if (!style.inlineIfBraceIfStatement) return out
        var result = out
        if (curr.type is TokenType.LeftBrace) result = result.space()
        return result
    }
}
