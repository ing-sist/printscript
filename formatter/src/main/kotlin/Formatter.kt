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

        println(rules)
        var curr = tokenStream.consume()
        var prev = Token(TokenType.EOF, "", Location(-1, -1, -1))

        while (curr.type !is TokenType.EOF) {
            val beforeNewline: Int? = applyBeforeNewlineRules(curr, style, out)
            val afterNewline = applyAfterNewlineRules(curr, style, out, tokenStream)
            val beforeSpacing = applyBeforeSpacingRules(curr, style)
            val afterSpacing = applyAfterSpacingRules(curr, style)

            // agrego newlines porque newlines > space
            if(beforeNewline != null) {
                repeat(beforeNewline) { out = out.newline() }
            }

            out = indentIfAtLineStart(out, curr.type, level, style)
            // aca ya imprimo token y espacio si corresponden
            if(beforeNewline == 0 && curr.type !is TokenType.Space) { // si no aplique newline, me fijo de espacio
                when (beforeSpacing) {
                    true -> { if(out.getLastSent().toString() != " "){ out = out.space() } } // si ya agregue un space, no agrego otro
                    false, null -> { }
                // si alguien me prohibe o da igual y no hay, no aplico
                }
            }

            if(curr.type !is TokenType.Space) {
                out = out.write(curr.lexeme)
            }

            if(curr.type is TokenType.Space) {
                val spaceAllowedAfterPrev = applyAfterSpacingRules(prev, style)
                val spaceAllowedBeforeNext = applyBeforeSpacingRules(tokenStream.peek(0), style)
                val keepSpace = (spaceAllowedAfterPrev == null) && (spaceAllowedBeforeNext == null)
                if (keepSpace && !out.isAtLineStart()) out = out.space()
            }

            if(afterNewline != null) {
                repeat(afterNewline) { out = out.newline() }
            }

            if(afterNewline == 0 && curr.type !is TokenType.Space) {
                when (afterSpacing) {
                    true -> { if(out.getLastSent().toString() != " "){ out = out.space() }
                    } // si ya agregue un space, no agrego otro
                    false, null -> { }
                    // si alguien me prohibe o da igual y no hay, no aplico
                }
            }

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

    private fun peekNextNonBlankType(ts: TokenStream): TokenType {
        var i = 0
        while (true) {
            val t = ts.peek(i)   // miro i tokens adelante
            when (t.type) {
                is TokenType.Space,
                is TokenType.Newline -> {
                    i++           // salto el espacio o newline
                    continue      // sigo buscando
                }
                else -> return t.type  // devuelvo lo primero que no sea espacio/newline
            }
        }
    }


    private fun applyBeforeNewlineRules(
        curr: Token,
        style: FormatterStyleConfig,
        out: DocBuilder
    ): Int? {
        var need = 0
        for (rule in rules) {
            if (rule is NewlineBeforeRule) {
                val v = rule.newlineBefore(curr, style, out)
                if (v > need) need = v
            }
        }
        return need
    }

    private fun applyAfterNewlineRules(
        curr: Token,
        style: FormatterStyleConfig,
        out: DocBuilder,
        tokenStream: TokenStream
    ): Int? {
        var need = 0
        for (rule in rules) {
            if (rule is NewlineAfterRule) {
                val v = rule.newlineAfter(curr, style, out)
                if (v > need) need = v
            }
        }
        val next = peekNextNonBlankType(tokenStream)
        if(next is TokenType.EOF) return 0
        return need
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

