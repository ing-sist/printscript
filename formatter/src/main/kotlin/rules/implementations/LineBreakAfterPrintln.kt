package rules.implementations

import DocBuilder
import Token
import config.FormatterStyleConfig

object LineBreakAfterPrintln : AfterRule {
    private var pendingPrintln = false

    override fun after(
        prev: Token,
        curr: Token,
        next: Token,
        style: FormatterStyleConfig,
        out: DocBuilder,
    ): DocBuilder {
        var result = out
        if (curr.lexeme.lowercase() == "println") {
            pendingPrintln = true
            return result
        } else {
            if (pendingPrintln && curr.type is TokenType.Semicolon) {
                // La regla general de "después de ;" ya habrá agregado el salto de línea normal
                repeat(style.lineBreakAfterPrintln) {
                    result = result.newline()
                }
                pendingPrintln = false
            }
            if (curr.type is TokenType.RightParen && pendingPrintln && next.type !is TokenType.Semicolon) {
                // Caso en que no hay ; después de println, sino un )
                repeat(style.lineBreakAfterPrintln) {
                    result = result.newline()
                }
                pendingPrintln = false
            }
        }

        return result
    }
}
