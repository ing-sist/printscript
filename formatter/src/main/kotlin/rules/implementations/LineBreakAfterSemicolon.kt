import config.StyleConfig
import rules.implementations.RuleImplementation

object LineBreakAfterSemicolon : RuleImplementation {
    override fun after(
        tokens: List<Token>,
        index: Int,
        style: StyleConfig,
        out: DocBuilder,
    ): DocBuilder {
        val token = tokens[index]
        val result = out
        if (token.type is TokenType.Semicolon && style.lineBreakAfterSemicolon) {
            result.newline()
        }
        return result
    }
}
