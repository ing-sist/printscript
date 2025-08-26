package style.policies

data class LineBreakPolicy(
    val afterSemicolon: Boolean = true,
    val afterOpenBrace: Boolean = true,
    val beforeCloseBrace: Boolean = true,
)
