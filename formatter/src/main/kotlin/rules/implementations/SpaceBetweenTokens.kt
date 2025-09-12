import config.StyleConfig
import rules.implementations.RuleImplementation

private val BLOCKED_TYPES =
    setOf(
        TokenType.Semicolon,
        TokenType.Comma,
        TokenType.EOF,
        TokenType.Colon,
    )

object SpaceBetweenTokens : RuleImplementation {
    override fun before(
        tokens: List<Token>,
        index: Int,
        style: StyleConfig,
        out: DocBuilder,
    ): DocBuilder {
        val current = tokens[index].type
        val isBlockedToken = current in BLOCKED_TYPES
        val shouldSkip = !style.spaceBetweenTokens || index == 0 || isBlockedToken

        if (shouldSkip) return out

        // de esto se encarga otra rule
//        if (prev is TokenType.LeftParen ||
//            prev is TokenType.LeftBrace) {
//            return out
//        }
//        if(prev is TokenType.Semicolon ||
//            prev is TokenType.Comma ||
//            prev is TokenType.Colon){
//            return out
//        }

        return out.space()
    }
}
