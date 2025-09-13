package rules.implementations

import DocBuilder
import Token
import TokenType
import config.FormatterStyleConfig

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
        style: FormatterStyleConfig,
        out: DocBuilder,
    ): DocBuilder {
        val token = tokens[index]
        var result = out
        if (token.type in operators && style.spaceAroundOperators) {
            result = result.space()
        }
        return result
    }

    override fun after(
        tokens: List<Token>,
        index: Int,
        style: FormatterStyleConfig,
        out: DocBuilder,
    ): DocBuilder {
        val token = tokens[index]
        var result = out
        if (token.type in operators && style.spaceAroundOperators) {
            result = result.space()
        }
        return result
    }
}
