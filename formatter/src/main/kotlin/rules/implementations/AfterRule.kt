package rules.implementations

import DocBuilder
import Token
import config.FormatterStyleConfig

interface AfterRule : RuleImplementation {
    fun after(
        prev: Token,
        curr: Token,
        next: Token,
        style: FormatterStyleConfig,
        out: DocBuilder,
    ): DocBuilder
}
