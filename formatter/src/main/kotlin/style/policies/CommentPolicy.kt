package style.policies

data class CommentPolicy(
    val type: CommentType = CommentType.EOL,
    val maxLength: Int = 100,
) : Policy

enum class CommentType {
    EOL, // End Of Line comments should stay at the end of the line
    OWN_LINE, // Comments should be moved to their own line
}
