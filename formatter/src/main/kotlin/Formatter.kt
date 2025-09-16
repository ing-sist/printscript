import config.FormatterStyleConfig
import rules.implementations.AfterRule
import rules.implementations.BeforeRule
import rules.implementations.RuleImplementation
import rules.implementations.SpaceForbid
import rules.implementations.SpaceIntent

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
        val spaceForbid = SpaceForbid()

        var prev = Token(TokenType.EOF, "", Location(-1, -1, -1))
        var curr = tokenStream.consume()

        while (curr.type !is TokenType.EOF) {
            val next: Token = tokenStream.peek(0)
            val prevOut = out
            out = applyBeforeRules(prev, curr, next, style, out, spaceForbid)

            out = indentIfAtLineStart(out, curr.type, level, style)

            if (out != prevOut && curr.type !is TokenType.Space && curr.type !is TokenType.Newline) {
                out = out.write(curr.lexeme)
            }
            if (out == prevOut && curr.type !is TokenType.Space && curr.type !is TokenType.Newline) {
                out = out.write(curr.lexeme)
            }

            if (curr.type is TokenType.Space) {
                val look = peekNextNonSpace(tokenStream)
                val intentOnly = SpaceForbid()
                lookAheadBeforeRules(prev, look, style, intentOnly)

                val noSpace = isForbidden(intentOnly, spaceForbid)

                if (!noSpace && !out.isAtLineStart()) out = out.space()

                while (tokenStream.peek(0).type is TokenType.Space) tokenStream.consume()
                curr = tokenStream.consume()

                spaceForbid.reset()
                continue
            }

            out = applyAfterRules(prev, curr, next, style, out, spaceForbid)

            level = updatedLevelAfter(curr.type, level)
            prev = curr
            curr = tokenStream.consume()
        }
        return out
    }

    private fun isForbidden(
        intentOnly: SpaceForbid,
        spaceForbid: SpaceForbid,
    ): Boolean {
        val forbidBefore = (intentOnly.beforeNext == SpaceIntent.FORBID)
        val forbidAfter = (spaceForbid.afterNext == SpaceIntent.FORBID)
        return forbidBefore || forbidAfter
    }

    private fun lookAheadBeforeRules(
        prev: Token,
        look: Token,
        style: FormatterStyleConfig,
        intentOnly: SpaceForbid,
    ) {
        var memoryBuilder = DocBuilder.inMemory()
        for (rule in rules) {
            if (rule is BeforeRule) {
                memoryBuilder = rule.before(prev, look, look, style, memoryBuilder, intentOnly)
            }
        }
    }

    private fun applyBeforeRules(
        prev: Token,
        curr: Token,
        next: Token,
        style: FormatterStyleConfig,
        out: DocBuilder,
        spaceForbid: SpaceForbid,
    ): DocBuilder {
        val acc = out
        for (rule in rules) {
            if (rule is BeforeRule) {
                val n = rule.before(prev, curr, next, style, acc, spaceForbid)
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
        spaceForbid: SpaceForbid,
    ): DocBuilder {
        val acc = out
        for (rule in rules) {
            if (rule is AfterRule) {
                val n = rule.after(prev, curr, next, style, acc, spaceForbid)
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

    fun peekNextNonSpace(ts: TokenStream): Token {
        var i = 0
        var t = ts.peek(0)
        while (t.type is TokenType.Space) {
            i++
            t = ts.peek(i)
        }
        return t
    }
}
