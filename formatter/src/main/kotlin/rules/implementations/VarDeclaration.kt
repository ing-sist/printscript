package rules.implementations

import DocBuilder
import Token
import TokenType
import config.FormatterStyleConfig

object VarDeclaration : RuleImplementation {
    override fun after(
        prev: Token,
        curr: Token,
        next: Token,
        style: FormatterStyleConfig,
        out: DocBuilder,
    ): DocBuilder {
        var result = out
        if (curr.type is TokenType.Keyword.VariableDeclaration) {
            result = result.space()
        }
        return result
    }
}
