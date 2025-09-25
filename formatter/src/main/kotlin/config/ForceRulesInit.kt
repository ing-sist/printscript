package config

import impl.BelowLineBraceIfStatement
import impl.Indentation
import impl.InlineBraceIfStatement
import impl.KeywordSpacingAfter
import impl.LineBreakAfterSemiColon
import impl.LineBreakBeforePrintln
//import impl.MaxSpaceBetweenTokens
import impl.SpaceAfterColon
import impl.SpaceAroundAssignment
import impl.SpaceAroundOperators
import impl.SpaceBeforeColon

object ForceRulesInit {
    fun loadAll() {
        KeywordSpacingAfter
        SpaceAroundAssignment
        InlineBraceIfStatement
        BelowLineBraceIfStatement
        Indentation
        LineBreakAfterSemiColon
        LineBreakBeforePrintln
//        MaxSpaceBetweenTokens
        SpaceAfterColon
        SpaceAroundOperators
        SpaceBeforeColon
    }
}