import config.FormatterStyleConfig
import config.IndentationDef
import impl.interfaces.NewlineAfterRule
import impl.interfaces.NewlineBeforeRule
import impl.interfaces.Rule
import impl.interfaces.SpaceAfterRule
import impl.interfaces.SpaceBeforeRule

class Formatter(
    private val rules: List<Rule>,
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
            // agrego newline porque newline > space
            val beforeNewline = applyBeforeNewlineRules(curr, style, out)
            repeat(beforeNewline) { out = out.newline() }

            out = indentIfAtLineStart(out, curr.type, level, style)


            val needBefore = applyBeforeSpacingRules(curr, style)
            val needAfter = applyAfterSpacingRules(curr, style)
            if(curr.type !is TokenType.Space){
                when (needBefore) {
                    false -> out = out.write(curr.lexeme)
                    null -> { out = out.write(curr.lexeme) }
                    true -> {
                        if(out.getLastSent().toString() != " "){
                            out = out.space()
                        }
                        out = out.write(curr.lexeme)
                    }
                }
                when (needAfter) {
                    true -> out = out.space()
                    null -> { }
                    false -> {

                    }
                }
            } else {
                val prevAfterSpace = applyAfterSpacingRules(prev, style)
                val nextBeforeSpace = applyBeforeSpacingRules(tokenStream.peek(0), style)
                if (out.getLastSent().toString() == " " || nextBeforeSpace == false || prevAfterSpace == false) {
                    prev = curr
                    curr = tokenStream.consume()
                    continue
                }
                out = out.space()
            }

            val afterNewline = applyAfterNewlineRules(curr, style, out)
            repeat(afterNewline) { out = out.newline() }

            level = updatedLevelAfter(curr.type, level)
            prev = curr
            curr = tokenStream.consume()
        }
        return out
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
        return out.indent(visibleLevel * (style[IndentationDef] ?: IndentationDef.default))
    }


    private fun applyBeforeNewlineRules(
        curr: Token,
        style: FormatterStyleConfig,
        out: DocBuilder
    ): Int {
        var needsNewline = 0
        for (rule in rules) {
            if (rule is NewlineBeforeRule) {
                needsNewline = rule.newlineBefore(curr, style, out)
            }
        }
        return needsNewline
    }

    private fun applyBeforeSpacingRules(
        curr: Token,
        style: FormatterStyleConfig,
    ): Boolean? {
        var putSpace: Boolean? = null
        for (rule in rules) {
            if (rule is SpaceBeforeRule) {
                when (rule.spaceBefore(curr, style)) {
                    null  -> { }           // no opina
                    false -> return false   // un NO gana siempre
                    true  -> putSpace = true
                }
            }
        }
        return if (putSpace == true) true else null
    }

    private fun applyAfterSpacingRules(
        curr: Token,
        style: FormatterStyleConfig,
    ): Boolean? {
        var putSpace: Boolean? = null
        for (rule in rules) {
            if (rule is SpaceAfterRule) {
                when (rule.spaceAfter(curr, style)) {
                    null  -> { }           // no opina
                    false -> return false   // un NO gana siempre
                    true  -> putSpace = true
                }
            }
        }
        return if (putSpace == true) true else null
    }

    private fun applyAfterNewlineRules(
        curr: Token,
        style: FormatterStyleConfig,
        out: DocBuilder
    ): Int {
        var needsNewline = 0
        for (rule in rules) {
            if (rule is NewlineAfterRule) {
                needsNewline = rule.newlineAfter(curr, style, out)
            }
        }
        return needsNewline
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