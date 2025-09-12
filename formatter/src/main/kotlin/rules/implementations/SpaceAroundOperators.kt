import config.StyleConfig
import rules.implementations.RuleImplementation

object SpaceAroundOperators : RuleImplementation {
    val operators =
        listOf<TokenType>(
            TokenType.Assignment,
            TokenType.Plus,
            TokenType.Minus,
            TokenType.Multiply,
            TokenType.Divide,
            TokenType.Equals,
            TokenType.NotEquals,
            TokenType.LessThan,
            TokenType.LessThanOrEqual,
            TokenType.GreaterThan,
            TokenType.GreaterThanOrEqual,
        )

    override fun before(
        tokens: List<Token>,
        index: Int,
        style: StyleConfig,
        out: DocBuilder,
    ): DocBuilder {
        val token = tokens[index]
        val result = out
        if (token.type in operators && style.spaceAroundOperators) {
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
        if (token.type in operators && style.spaceAroundOperators) {
            result.space()
        }
        return result
    }
}
