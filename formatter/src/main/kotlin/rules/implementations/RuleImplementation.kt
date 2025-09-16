package rules.implementations

import DocBuilder
import Token
import config.FormatterStyleConfig

interface RuleImplementation

interface BeforeRule : RuleImplementation {
    fun before(
        prev: Token,
        curr: Token,
        next: Token,
        style: FormatterStyleConfig,
        out: DocBuilder,
        spaceForbid: SpaceForbid,
    ): DocBuilder
}

interface AfterRule : RuleImplementation {
    fun after(
        prev: Token,
        curr: Token,
        next: Token,
        style: FormatterStyleConfig,
        out: DocBuilder,
        spaceForbid: SpaceForbid,
    ): DocBuilder
}
