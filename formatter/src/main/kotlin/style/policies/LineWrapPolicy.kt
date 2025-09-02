package style.policies

data class LineWrapPolicy(
    val mode: LineWrapMode = LineWrapMode.IF_TOO_LONG,
    val maxLineLength: Int = 100,
    val continuationIndent: Int = 2,
) : Policy

enum class LineWrapMode { NEVER, IF_TOO_LONG, ALWAYS }
