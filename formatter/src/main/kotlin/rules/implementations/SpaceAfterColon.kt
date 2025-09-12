import config.StyleConfig
import rules.implementations.RuleImplementation

object SpaceAfterColon : RuleImplementation {
    override fun before(
        tokens: List<Token>,
        index: Int,
        style: StyleConfig,
        out: DocBuilder,
    ): DocBuilder {
        val token = tokens[index]
        val result = out
        if (token.type is TokenType.Colon && style.spaceAfterColon) {
            result.space()
        }
        return result
    }
}
