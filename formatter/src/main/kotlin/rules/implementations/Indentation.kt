import config.FormatterStyleConfig
import rules.implementations.AfterRule
import rules.implementations.BeforeRule

object Indentation : BeforeRule, AfterRule {
    override fun before(
        prev: Token,
        curr: Token,
        next: Token,
        style: FormatterStyleConfig,
        out: DocBuilder,
    ): DocBuilder {
        var result = out
        if (style.indentation == 0) return out

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
        if (style.indentation == 0) return out
        var result = out
        if (curr.type is TokenType.LeftBrace && !result.isAtLineStart()) {
            result = result.newline()
        }
        return result
    }
}
