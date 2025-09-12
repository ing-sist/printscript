import config.StyleConfig
import rules.implementations.RuleImplementation

object SpaceAroundAsignement : RuleImplementation {
    override fun before(
        tokens: List<Token>,
        index: Int,
        style: StyleConfig,
        out: DocBuilder,
    ): DocBuilder {
        val token = tokens[index]
        val result = out
        if (token.type is TokenType.Assignment && style.spaceAroundAssignment) {
            result.space()
        }
        return result
    }

    override fun after(
        tokens: List<Token>,
        index: Int,
        style: StyleConfig,
        out: DocBuilder,
    ): DocBuilder {
        val token = tokens[index]
        val result = out
        if (token.type is TokenType.Assignment && style.spaceAroundAssignment) {
            result.space()
        }
        return result
    }
}
