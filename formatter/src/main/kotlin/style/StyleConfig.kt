package style

import style.policies.ArgsLayoutPolicy
import style.policies.BlankLinePolicy
import style.policies.CommentPolicy
import style.policies.IndentationPolicy
import style.policies.LineBreakPolicy
import style.policies.LineWrapPolicy
import style.policies.SpacingPolicy
import style.policies.WhitespaceTrimPolicy

data class StyleConfig(
    val spacing: SpacingPolicy = SpacingPolicy(),
    val lineBreak: LineBreakPolicy = LineBreakPolicy(),
    val lineWrap: LineWrapPolicy = LineWrapPolicy(),
    val argsLayout: ArgsLayoutPolicy = ArgsLayoutPolicy.INLINE,
    val blankLines: BlankLinePolicy = BlankLinePolicy.AT_MOST_ONE,
    val comments: CommentPolicy = CommentPolicy(),
    val indentation: IndentationPolicy = IndentationPolicy(),
    val whitespaceTrim: WhitespaceTrimPolicy = WhitespaceTrimPolicy.TRAILING,
)
