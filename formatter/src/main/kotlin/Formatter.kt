import config.FormatterStyleConfig
import rules.implementations.AfterRule
import rules.implementations.BeforeRule
import rules.implementations.RuleImplementation

class Formatter(
    private val rules: List<RuleImplementation>,
) {
    fun format(
        tokenStream: TokenStream,
        style: FormatterStyleConfig,
        initial: DocBuilder,
    ): DocBuilder {
        var out = initial
        var level = 0

        var prev = Token(TokenType.EOF, "", Location(-1, -1, -1))
        var curr = tokenStream.consume()

        while (curr.type !is TokenType.EOF) {
            val next: Token = tokenStream.peek(0)
            val prevOut = out
            out = applyBeforeRules(prev, curr, next, style, out)

            out = indentIfAtLineStart(out, curr.type, level, style)

            if (out != prevOut && curr.type !is TokenType.Space) {
                out = out.write(curr.lexeme)
            }
            if (out == prevOut && curr.type !is TokenType.Space) {
                out = out.write(curr.lexeme)
            }

            if (out == prevOut && !out.isAtLineStart()) {
                out = out.write(curr.lexeme)
            }

            out = applyAfterRules(prev, curr, next, style, out)

            level = updatedLevelAfter(curr.type, level)
            prev = curr
            curr = tokenStream.consume()
        }
        return out
    }

    private fun applyBeforeRules(
        prev: Token,
        curr: Token,
        next: Token,
        style: FormatterStyleConfig,
        out: DocBuilder,
    ): DocBuilder {
        val acc = out
        for (rule in rules) {
            if (rule is BeforeRule) {
                val n = rule.before(prev, curr, next, style, acc)
                if (n != acc) return n
            }
        }
        return acc
    }

    private fun applyAfterRules(
        prev: Token,
        curr: Token,
        next: Token,
        style: FormatterStyleConfig,
        out: DocBuilder,
    ): DocBuilder {
        val acc = out
        for (rule in rules) {
            if (rule is AfterRule) {
                val n = rule.after(prev, curr, next, style, acc)
                if (n != acc) return n
            }
        }
        return acc
    }

    private fun indentIfAtLineStart(
        out: DocBuilder,
        currType: TokenType,
        level: Int,
        style: FormatterStyleConfig,
    ): DocBuilder {
        if (!out.isAtLineStart()) return out
        val visibleLevel =
            if (currType is TokenType.RightBrace) (level - 1).coerceAtLeast(0) else level
        return out.indent(visibleLevel * style.indentation)
    }

    private fun updatedLevelAfter(
        currType: TokenType,
        level: Int,
    ): Int =
        when (currType) {
            is TokenType.LeftBrace -> level + 1
            is TokenType.RightBrace -> (level - 1).coerceAtLeast(0)
            else -> level
        }
}
