import config.StyleConfig
import rules.implementations.RuleImplementation
import rules.implementations.isIfConditionBeforeLeftBrace

object InlineBraceIfStatement : RuleImplementation {
    override fun before(
        tokens: List<Token>,
        index: Int,
        style: StyleConfig,
        out: DocBuilder,
    ): DocBuilder {
        val token = tokens[index]
        val result = out
        if ((token.type !is TokenType.LeftBrace) || (!isIfConditionBeforeLeftBrace(tokens, index))) return result

        return result.space() // si es un if statement, agrego espacop if(..) {
    }
}
