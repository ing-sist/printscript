package rules.implementations

import DocBuilder
import Token
import config.FormatterStyleConfig

interface RuleImplementation {
    fun before(
        tokens: List<Token>,
        index: Int,
        style: FormatterStyleConfig,
        out: DocBuilder,
    ): DocBuilder = out

    fun after(
        tokens: List<Token>,
        index: Int,
        style: FormatterStyleConfig,
        out: DocBuilder,
    ): DocBuilder = out
}
