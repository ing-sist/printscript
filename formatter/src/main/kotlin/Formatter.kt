import config.FormatterStyleConfig
import rules.implementations.RuleImplementation

class Formatter(
    private val rules: List<RuleImplementation>,
) {
    fun format(
        tokens: List<Token>,
        style: FormatterStyleConfig,
        initial: DocBuilder,
    ): DocBuilder {
        var out = initial

        for (i in tokens.indices) {
            for (rule in rules) {
                val newOut = rule.before(tokens, i, style, out)

                if (newOut != out) {
                    out = newOut
                    break
                }
            }

            if (tokens[i].type !is TokenType.EOF) {
                if (out.isAtLineStart()) {
                    val level = indentLevelUpTo(tokens, i)
                    out = out.indent(level * style.indentation)
                }
                out = out.write(tokens[i].lexeme)
            }

            for (rule in rules) {
                val newOut = rule.after(tokens, i, style, out)

                if (newOut != out) {
                    out = newOut
                    break
                }
            }
        }
        return out
    }
}
