package style.policies

data class BracePolicy(
    val spaceBeforeOpening: Boolean = true,
    val spaceAfterOpening: Boolean = false,
    val spaceBeforeClosing: Boolean = false,
    val newlineAfterOpening: Boolean = true,
    val newlineBeforeClosing: Boolean = true,
    val indentBetweenBraces: Boolean = true,
) {
    companion object {
        val KR =
            BracePolicy(
                spaceBeforeOpening = true,
                newlineAfterOpening = true,
                newlineBeforeClosing = true,
                indentBetweenBraces = true,
            )

        val Allman =
            BracePolicy(
                spaceBeforeOpening = false, // no espacio, la llave va en otra línea
                newlineAfterOpening = true,
                newlineBeforeClosing = true,
                indentBetweenBraces = true,
            )

        val Compact =
            BracePolicy(
                spaceBeforeOpening = false,
                spaceAfterOpening = false,
                spaceBeforeClosing = false,
                newlineAfterOpening = false,
                newlineBeforeClosing = false,
                indentBetweenBraces = true,
            )
    }
}
