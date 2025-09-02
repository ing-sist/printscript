package style.policies

data class LineBreakPolicy(
    val beforeBinaryOp: LineBreakType = LineBreakType.IF_TOO_LONG,
    val afterBinaryOp: LineBreakType = LineBreakType.IF_TOO_LONG,
    val beforeAssignment: LineBreakType = LineBreakType.IF_TOO_LONG,
    val afterAssignment: LineBreakType = LineBreakType.IF_TOO_LONG,
    val beforeComma: LineBreakType = LineBreakType.NEVER,
    val afterComma: LineBreakType = LineBreakType.ALWAYS,
    val beforeParen: LineBreakType = LineBreakType.NEVER,
    val afterParen: LineBreakType = LineBreakType.NEVER,
    val beforeBracket: LineBreakType = LineBreakType.NEVER,
    val afterBracket: LineBreakType = LineBreakType.NEVER,
    val beforeKeyword: LineBreakType = LineBreakType.NEVER,
    val afterKeyword: LineBreakType = LineBreakType.ALWAYS,
    val beforeColon: LineBreakType = LineBreakType.NEVER,
    val afterColon: LineBreakType = LineBreakType.ALWAYS,
    val beforeSemicolon: LineBreakType = LineBreakType.NEVER,
    val afterSemicolon: LineBreakType = LineBreakType.NEVER,
) : Policy

enum class LineBreakType {
    ALWAYS,
    NEVER,
    IF_TOO_LONG,
}
