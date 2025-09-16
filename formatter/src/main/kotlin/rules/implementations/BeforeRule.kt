package rules.implementations

import DocBuilder
import Token
import config.FormatterStyleConfig

interface BeforeRule : RuleImplementation {
    fun before(
        prev: Token,
        curr: Token,
        next: Token,
        style: FormatterStyleConfig,
        out: DocBuilder,
    ): DocBuilder
}
