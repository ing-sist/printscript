package style

import style.policies.ArgsLayoutPolicy
import style.policies.BlankLinePolicy
import style.policies.BracePolicy
import style.policies.CommentPolicy
import style.policies.IndentationPolicy
import style.policies.LineBreakPolicy
import style.policies.LineWrapPolicy
import style.policies.ParenPolicy
import style.policies.SpacingPolicy
import style.policies.WhitespaceTrimPolicy

data class StyleConfig(
    val indent: IndentationPolicy = IndentationPolicy.Spaces(4),
    val lineWrap: LineWrapPolicy = LineWrapPolicy.Soft(120),
    val insertFinalNewline: Boolean = true,
    val whitespaceTrim: WhitespaceTrimPolicy = WhitespaceTrimPolicy.TRAILING_AND_FINAL_EOL,
    val spacing: SpacingPolicy = SpacingPolicy(),
    val lineBreaks: LineBreakPolicy = LineBreakPolicy(),
    val comments: CommentPolicy = CommentPolicy(),
    val argsLayout: ArgsLayoutPolicy = ArgsLayoutPolicy.INLINE,
    val blankLines: BlankLinePolicy = BlankLinePolicy(1),
    val parenPolicy: ParenPolicy = ParenPolicy(),
    val bracePolicy: BracePolicy = BracePolicy(),
) {
    init {
        if (indent is IndentationPolicy.Spaces && indent.size <= 0) {
            StyleError.IndentSizeError("Indent size must be > 0")
        }
        if (lineWrap is LineWrapPolicy.Soft && lineWrap.limit <= 0) {
            StyleError.LineWrapError("Soft line wrap limit must be > 0")
        }
        if (lineWrap is LineWrapPolicy.Hard && lineWrap.limit <= 0) {
            StyleError.LineWrapError("Hard line wrap limit must be > 0")
        }
    }
}
