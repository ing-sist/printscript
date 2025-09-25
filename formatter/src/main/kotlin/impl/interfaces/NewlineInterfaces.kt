package impl.interfaces

import DocBuilder
import Token
import config.FormatterStyleConfig

interface NewlineRulesImpl : Rule

interface NewlineBeforeRule : NewlineRulesImpl {
    fun newlineBefore(
        curr: Token,
        style: FormatterStyleConfig,
        out: DocBuilder
    ): Int
}

interface NewlineAfterRule : NewlineRulesImpl {
    fun newlineAfter(
        curr: Token,
        style: FormatterStyleConfig,
        out: DocBuilder
    ): Int
}
