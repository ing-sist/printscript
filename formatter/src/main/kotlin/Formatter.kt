import config.FormatterStyleConfig
import impl.interfaces.Rule

class Formatter(
    private val rules: List<Rule>,
    private val rulesEngine: RulesEngine = RulesEngine(rules),
) {
    fun format(
        tokenStream: TokenStream,
        style: FormatterStyleConfig,
        initial: DocBuilder,
    ): DocBuilder {
        var state = FormatterState(initial, 0)

        var curr = tokenStream.consume()
        var prev = Token(TokenType.EOF, "", Location(-1, -1, -1))

        while (curr.type !is TokenType.EOF) {
            state = rulesEngine.applyBeforeWhiteSpaces(curr, style, state)
            state = rulesEngine.adjustBeforeLevel(curr, state, style)
            state = rulesEngine.writeToken(prev, curr, state, style, tokenStream)
            state = rulesEngine.applyAfterWhiteSpaces(curr, style, state, tokenStream)
            state = rulesEngine.adjustAfterLevel(curr, state)
            prev = curr
            curr = tokenStream.consume()
        }
        return state.out
    }
}
