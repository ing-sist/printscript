package style.policies

data class IndentationPolicy(
    val size: Int = 4,
    val continuationIndent: Int = 2,
) : Policy
