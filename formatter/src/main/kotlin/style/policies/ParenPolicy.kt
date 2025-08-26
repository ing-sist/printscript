package style.policies

data class ParenPolicy(
    val spaceBeforeOpening: Boolean = false, // f (x) o f(x)
    val spaceAfterOpening: Boolean = false, // ( x  (x
    val spaceBeforeClosing: Boolean = false, // x )  x)
    val spaceInsideEmpty: Boolean = false, // ( ) vs ()
    val newlineAfterOpening: Boolean = false,
    val newlineBeforeClosing: Boolean = false,
    val indentBetweenParens: Boolean = false,
) {
    companion object {
        val Compact = ParenPolicy()
        val SpaceBeforeOpen = ParenPolicy(spaceBeforeOpening = true)
        val MultilineIndented =
            ParenPolicy(
                newlineAfterOpening = true,
                newlineBeforeClosing = true,
                indentBetweenParens = true,
            )
    }
    // uso parenpolicy.compact en style config por ej
}
