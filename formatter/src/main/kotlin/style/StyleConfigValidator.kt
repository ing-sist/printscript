package style

import Result
import style.policies.IndentationPolicy
import style.policies.LineWrapPolicy

// no puedo importar el result q hizo facu en lexer, para poder reutilizarlo

typealias ValidationResult = Result<Unit, List<StyleError>>

object StyleConfigValidator {
    fun validate(config: StyleConfig): ValidationResult {
        val errors =
            buildList {
                addAll(validateIndent(config))
                addAll(validateLineWrap(config))
                addAll(validateBlankLines(config))
            }
        return if (errors.isEmpty()) Result.Success(Unit) else Result.Failure(errors)
    }

    private fun validateIndent(config: StyleConfig): List<StyleError> {
        val indent = config.indent
        return if (indent is IndentationPolicy.Spaces && indent.size <= 0) {
            listOf(StyleError.IndentSizeError("Indent size must be > 0 (was ${indent.size})"))
        } else {
            emptyList()
        }
    }

    private fun validateLineWrap(config: StyleConfig): List<StyleError> =
        when (val lw = config.lineWrap) {
            is LineWrapPolicy.Soft ->
                if (lw.limit <= 0) {
                    listOf(StyleError.LineWrapError("Soft line wrap limit must be > 0 (was ${lw.limit})"))
                } else {
                    emptyList()
                }
            is LineWrapPolicy.Hard ->
                if (lw.limit <= 0) {
                    listOf(StyleError.LineWrapError("Hard line wrap limit must be > 0 (was ${lw.limit})"))
                } else {
                    emptyList()
                }
            LineWrapPolicy.Off -> emptyList()
        }

    private fun validateBlankLines(config: StyleConfig): List<StyleError> {
        val n = config.blankLines.preserveBlankLinesUpTo
        return if (n < 0) {
            listOf(
                StyleError.BlankLinesError("preserveBlankLinesUpTo must be >= 0 (was $n)"),
            )
        } else {
            emptyList()
        }
    }
}
