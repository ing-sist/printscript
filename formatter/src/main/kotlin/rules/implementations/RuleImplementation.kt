package rules.implementations

import DocBuilder
import Token
import config.FormatterStyleConfig

interface RuleImplementation {
    fun before(
        prev: Token,
        curr: Token,
        next: Token,
        style: FormatterStyleConfig,
        out: DocBuilder,
    ): DocBuilder = out

    fun after(
        prev: Token,
        curr: Token,
        next: Token,
        style: FormatterStyleConfig,
        out: DocBuilder,
    ): DocBuilder = out
}
