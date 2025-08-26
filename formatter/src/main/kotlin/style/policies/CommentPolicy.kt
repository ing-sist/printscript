package style.policies

data class CommentPolicy(
    val allowEndOfLine: Boolean = true,
)

// end of line es val x = 10 // comment, si es false el comment va en la linea siguiente
