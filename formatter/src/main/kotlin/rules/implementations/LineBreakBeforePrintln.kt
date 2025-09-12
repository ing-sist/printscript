import config.StyleConfig
import rules.implementations.RuleImplementation

object LineBreakBeforePrintln : RuleImplementation {
    override fun before(
        tokens: List<Token>,
        index: Int,
        style: StyleConfig,
        out: DocBuilder,
    ): DocBuilder {
        val token = tokens[index]
        val result = out
        if (token.lexeme == "println") {
            repeat(style.lineBreakBeforePrintln) {
                result.newline()
            }
        }
        return result
    }
}
