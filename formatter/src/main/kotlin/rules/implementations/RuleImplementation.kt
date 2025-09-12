package rules.implementations

import DocBuilder
import Token
import config.StyleConfig

interface RuleImplementation {
    fun before(
        tokens: List<Token>,
        index: Int,
        style: StyleConfig,
        out: DocBuilder,
    ): DocBuilder = out

    fun after(
        tokens: List<Token>,
        index: Int,
        style: StyleConfig,
        out: DocBuilder,
    ): DocBuilder = out
}
