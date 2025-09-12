import config.StyleConfig
import rules.implementations.RuleImplementation

object SpaceBeforeColon : RuleImplementation {
    override fun before(
        tokens: List<Token>,
        index: Int,
        style: StyleConfig,
        out: DocBuilder,
    ): DocBuilder {
        val token = tokens[index]
        val result = out
        if (token.type is TokenType.Colon && style.spaceBeforeColon) {
            result.space()
        }
        return result
    }
}
