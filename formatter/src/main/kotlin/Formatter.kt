import config.StyleConfig
import rules.implementations.RuleImplementation

class Formatter(
    private val rules: List<RuleImplementation>,
) {
    fun format(
        tokens: List<Token>,
        style: StyleConfig,
        initial: DocBuilder,
    ): DocBuilder {
        var out = initial
        for (i in tokens.indices) {
            for (rule in rules) out = rule.before(tokens, i, style, out)

            if (tokens[i].type !is TokenType.EOF) {
                out = out.write(tokens[i].lexeme)
            }

            for (rule in rules) out = rule.after(tokens, i, style, out)
        }
        return out
    }
}
