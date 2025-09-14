package rules.implementations

import DocBuilder
import Token
import TokenType
import config.FormatterStyleConfig

object SpaceAroundOperators : BeforeRule, AfterRule {
    val operators =
        listOf<TokenType>(
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
        prev: Token,
        curr: Token,
        next: Token,
        style: FormatterStyleConfig,
        out: DocBuilder,
    ): DocBuilder {
        var result = out
        if (curr.type in operators && style.spaceAroundOperators) {
            result = result.space()
        }
        return result
    }

    override fun after(
        prev: Token,
        curr: Token,
        next: Token,
        style: FormatterStyleConfig,
        out: DocBuilder,
    ): DocBuilder {
        var result = out
        if (curr.type in operators && style.spaceAroundOperators) {
            result = result.space()
        }
        return result
    }
}
