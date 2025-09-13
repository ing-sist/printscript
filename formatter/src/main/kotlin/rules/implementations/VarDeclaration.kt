package rules.implementations

import DocBuilder
import Token
import TokenType
import config.FormatterStyleConfig

object VarDeclaration : RuleImplementation {
    override fun after(
        tokens: List<Token>,
        index: Int,
        style: FormatterStyleConfig,
        out: DocBuilder,
    ): DocBuilder {
        val token = tokens[index]
        var result = out
        if (token.type is TokenType.Keyword.VariableDeclaration) {
            result = result.space()
        }
        return result
    }
}
