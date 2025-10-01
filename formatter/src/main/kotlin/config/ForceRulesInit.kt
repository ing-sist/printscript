package config

import MaxSpaceBetweenTokens
import impl.newlines.BelowLineBraceIfStatement
import impl.newlines.BraceLineBreak
import impl.spaces.InlineBraceIfStatement
import impl.spaces.KeywordSpacingAfter
import impl.newlines.LineBreakAfterSemiColon
import impl.newlines.LineBreakBeforePrintln
import impl.spaces.SpaceAfterColon
import impl.spaces.SpaceAroundAssignment
import impl.spaces.SpaceAroundOperators
import impl.spaces.SpaceBeforeColon

object ForceRulesInit {
    fun loadAll() {
        KeywordSpacingAfter
        SpaceAroundAssignment
        InlineBraceIfStatement
        BelowLineBraceIfStatement
        BraceLineBreak
        LineBreakAfterSemiColon
        LineBreakBeforePrintln
        MaxSpaceBetweenTokens
        SpaceAfterColon
        SpaceAroundOperators
        SpaceBeforeColon
    }
}