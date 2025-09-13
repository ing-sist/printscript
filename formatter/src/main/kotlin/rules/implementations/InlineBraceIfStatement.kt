import config.FormatterStyleConfig
import rules.implementations.RuleImplementation

object InlineBraceIfStatement : RuleImplementation {
    override fun before(
        tokens: List<Token>,
        index: Int,
        style: FormatterStyleConfig,
        out: DocBuilder,
    ): DocBuilder {
        val token = tokens[index]
        var result = out
        if (token.type is TokenType.LeftBrace) result = result.space()
        return result
    }
}
