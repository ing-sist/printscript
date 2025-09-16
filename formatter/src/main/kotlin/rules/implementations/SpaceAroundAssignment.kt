package rules.implementations

import DocBuilder
import Token
import TokenType
import config.FormatterStyleConfig

object SpaceAroundAssignment : BeforeRule, AfterRule {
    override fun before(
        prev: Token,
        curr: Token,
        next: Token,
        style: FormatterStyleConfig,
        out: DocBuilder,
        spaceForbid: SpaceForbid,
    ): DocBuilder {
        var result = out
        if (curr.type is TokenType.Assignment) {
            if (style.spaceAroundAssignment) {
                // EXACTAMENTE un espacio antes de '='
                if (!result.isAtLineStart()) {
                    result = result.space()
                }
                // Bloquea el hueco prev..curr para que el branch de Space no agregue otro
                spaceForbid.forbidBefore()
            } else {
                // Ningún espacio antes de '='
                spaceForbid.forbidBefore()
            }
        }
        return result
    }

    override fun after(
        prev: Token,
        curr: Token,
        next: Token,
        style: FormatterStyleConfig,
        out: DocBuilder,
        spaceForbid: SpaceForbid,
    ): DocBuilder {
        var result = out
        if (curr.type is TokenType.Assignment && style.spaceAroundAssignment && next.type !is TokenType.Space) {
            result = result.space()
            spaceForbid.forbidAfter()
        }

        if (curr.type is TokenType.Assignment && !style.spaceAroundAssignment) {
            spaceForbid.forbidAfter()
        }
        return result
    }
}
