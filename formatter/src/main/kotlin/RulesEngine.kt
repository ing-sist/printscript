import config.FormatterStyleConfig
import config.IndentationDef
import impl.interfaces.NewlineAfterRule
import impl.interfaces.NewlineBeforeRule
import impl.interfaces.Rule
import impl.interfaces.SpaceAfterRule
import impl.interfaces.SpaceBeforeRule

class RulesEngine(
    private val rules: List<Rule>,
) {
    fun writeToken(
        prev: Token,
        curr: Token,
        state: FormatterState,
        style: FormatterStyleConfig,
        tokenStream: TokenStream,
    ): FormatterState {
        var newOut = state.out
        val beforeSpacing = applyBeforeSpacingRules(curr, style)
        val beforeNewline: Int? = applyBeforeNewlineRules(curr, style, state.out)
        // aca ya imprimo token y espacio si corresponden
        if (beforeNewline == 0 && curr.type !is TokenType.Space && !state.out.isAtLineStart()) {
            // si no aplique newline, me fijo de espacio
            when (beforeSpacing) {
                true -> if (!state.out.lastWasSpace()) newOut = newOut.space()
                // si ya agregue un space, no agrego otro
                false, null -> {
                }
                // si alguien me prohibe o da igual y no hay, no aplico
            }
        }

        if (curr.type !is TokenType.Space && curr.type !is TokenType.Newline) {
            newOut = newOut.write(curr.lexeme)
        }

        newOut = writeSpace(prev, curr, newOut, style, tokenStream)
        return FormatterState(newOut, state.level)
    }

    private fun writeSpace(
        prev: Token,
        curr: Token,
        out: DocBuilder,
        style: FormatterStyleConfig,
        tokenStream: TokenStream,
    ): DocBuilder {
        var newOut = out
        if (curr.type is TokenType.Space && !out.lastWasNewline()) {
            val prevIsRightBrace = prev.type is TokenType.RightBrace

            val willBreakBeforeNext =
                applyBeforeNewlineRules(tokenStream.peek(0), style, out) > 0
            while (tokenStream.peek(0).type is TokenType.Space) {
                tokenStream.consume()
            }

            val spaceAllowedAfterPrev = applyAfterSpacingRules(prev, style)
            val spaceAllowedBeforeNext = applyBeforeSpacingRules(tokenStream.peek(0), style)
            val keepSpace = (spaceAllowedAfterPrev == null) && (spaceAllowedBeforeNext == null)
            val lastNoWhitespace = !out.isAtLineStart() && !out.lastWasSpace()
            val putNoSpaceBefAndAftCond = !prevIsRightBrace && !willBreakBeforeNext
            if (keepSpace && lastNoWhitespace && putNoSpaceBefAndAftCond) {
                newOut = newOut.space()
            }
        }
        return newOut
    }

    fun applyBeforeWhiteSpaces(
        curr: Token,
        style: FormatterStyleConfig,
        state: FormatterState,
    ): FormatterState {
        var newOut = state.out
        val beforeNewline: Int? = applyBeforeNewlineRules(curr, style, state.out)
        if (beforeNewline != null && curr.type !is TokenType.Newline) {
            repeat(beforeNewline) { newOut = newOut.newline() }
        }
        return FormatterState(newOut, state.level)
    }

    fun adjustBeforeLevel(
        curr: Token,
        state: FormatterState,
        style: FormatterStyleConfig,
    ): FormatterState {
        var newLevel = state.level
        var newOut = state.out
        if (state.out.isAtLineStart() && curr.type !is TokenType.Space) {
            if (curr.type is TokenType.RightBrace) {
                newLevel =
                    if (state.level == 0) {
                        0
                    } else {
                        state.level - 1
                    }
            }
            val indentSize = style[IndentationDef] ?: IndentationDef.default
            newOut = newOut.indent(newLevel * indentSize)
        }
        return FormatterState(newOut, newLevel)
    }

    fun adjustAfterLevel(
        curr: Token,
        state: FormatterState,
    ): FormatterState {
        var newLevel = state.level
        newLevel =
            when (curr.type) {
                is TokenType.LeftBrace -> newLevel + 1
                is TokenType.RightBrace -> (newLevel - 1).coerceAtLeast(0)
                else -> newLevel
            }
        return FormatterState(state.out, newLevel)
    }

    fun applyAfterWhiteSpaces(
        curr: Token,
        style: FormatterStyleConfig,
        state: FormatterState,
        tokenStream: TokenStream,
    ): FormatterState {
        var newOut = state.out
        val afterNewline = applyAfterNewlineRules(curr, style, state.out, tokenStream)
        val afterSpacing = applyAfterSpacingRules(curr, style)
        if (afterNewline != null) {
            repeat(afterNewline) { newOut = newOut.newline() }
        }
        if (afterNewline == 0 && curr.type !is TokenType.Space) {
            when (afterSpacing) {
                true -> if (!state.out.lastWasSpace()) newOut = newOut.space()
                // si ya agregue un space, no agrego otro
                false, null -> { }
                // si alguien me prohibe o da igual y no hay, no aplico
            }
        }
        return FormatterState(newOut, state.level)
    }

    private fun applyBeforeNewlineRules(
        curr: Token,
        style: FormatterStyleConfig,
        out: DocBuilder,
    ): Int {
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
        tokenStream: TokenStream,
    ): Int? {
        var need = 0
        for (rule in rules) {
            if (rule is NewlineAfterRule) {
                val v = rule.newlineAfter(curr, style, out)
                if (v > need) need = v
            }
        }
        var i = 0
        var nextType: TokenType
        while (true) {
            val t = tokenStream.peek(i)
            val tt = t.type

            val isBlank = tt is TokenType.Space || tt is TokenType.Newline
            if (!isBlank) {
                nextType = tt
                break
            }

            i++
        }

        if (nextType is TokenType.EOF) return 0
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
                    null -> { } // no opina
                    false -> return false // un NO gana siempre
                    true -> putSpace = true
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
                    null -> { } // no opina
                    false -> return false // un NO gana siempre
                    true -> putSpace = true
                }
            }
        }
        return if (putSpace == true) true else null
    }
}
