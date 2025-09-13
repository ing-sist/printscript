import config.FormatterStyleConfig
import rules.implementations.RuleImplementation

object Indentation : RuleImplementation {
    override fun before(
        prev: Token,
        curr: Token,
        next: Token,
        style: FormatterStyleConfig,
        out: DocBuilder,
    ): DocBuilder {
        var result = out

        if (curr.type is TokenType.RightBrace) {
            if (prev.type !is TokenType.Semicolon) {
                result = result.newline()
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
        if (curr.type is TokenType.LeftBrace && !result.isAtLineStart()) {
            result = result.newline()
        }
        return result
    }
}
