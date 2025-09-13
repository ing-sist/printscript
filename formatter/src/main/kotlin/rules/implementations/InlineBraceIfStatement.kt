import config.FormatterStyleConfig
import rules.implementations.RuleImplementation

object InlineBraceIfStatement : RuleImplementation {
    override fun before(
        prev: Token,
        curr: Token,
        next: Token,
        style: FormatterStyleConfig,
        out: DocBuilder,
    ): DocBuilder {
        var result = out
        if (curr.type is TokenType.LeftBrace) result = result.space()
        return result
    }
}
