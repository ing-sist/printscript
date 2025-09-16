package rules.implementations

import DocBuilder
import Token
import TokenType
import config.FormatterStyleConfig

object KeywordSpacing : BeforeRule, AfterRule {
    override fun before(
        prev: Token,
        curr: Token,
        next: Token,
        style: FormatterStyleConfig,
        out: DocBuilder,
        spaceForbid: SpaceForbid,
    ): DocBuilder {
        var out = out
        if (curr.type is TokenType.Keyword && !out.isAtLineStart()) {
            if (spaceForbid.beforeNext != SpaceIntent.FORBID) {
                out = out.space()
                spaceForbid.forbidBefore()
            }
        }
        return out
    }

    override fun after(
        prev: Token,
        curr: Token,
        next: Token,
        style: FormatterStyleConfig,
        out: DocBuilder,
        spaceForbid: SpaceForbid,
    ): DocBuilder {
        var out = out
        if (curr.type !is TokenType.Keyword) return out

        val needSpaceAfter =
            when (curr.type) {
                is TokenType.Keyword.If -> next.type is TokenType.LeftParen
                is TokenType.Keyword.VariableDeclaration -> next.type is TokenType.Identifier
                else -> false
            }
        if (needSpaceAfter) {
            out = out.space()
            spaceForbid.forbidAfter()
        }
        return out
    }
}
