package config

import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.intOrNull

interface RuleDef<T> {
    val default: T?
    val id: String

    fun parse(json: JsonPrimitive): T
}


object SpaceAroundOperatorsDef : RuleDef<Boolean> {
    override val default = true
    override val id: String = "SpaceAroundOperators"

    

    override fun parse(json: JsonPrimitive): Boolean =
        json.booleanOrNull
            ?: error("space around operator's rule must be a boolean")
}

object SpaceBeforeColonDef : RuleDef<Boolean> {
    override val default = null
    override val id: String = "SpaceBeforeColon"

    

    override fun parse(json: JsonPrimitive): Boolean =
        json.booleanOrNull
            ?: error("space before colon's rule must be a boolean")
}

object SpaceAfterColonDef : RuleDef<Boolean> {
    override val default = null
    override val id: String = "SpaceAfterColon"

    

    override fun parse(json: JsonPrimitive): Boolean =
        json.booleanOrNull
            ?: error("space after colon's rule must be a boolean")
}

object SpaceAroundAssignmentDef : RuleDef<Boolean> {
    override val default = null
    override val id: String = "SpaceAroundAssignment"

    

    override fun parse(json: JsonPrimitive): Boolean =
        json.booleanOrNull
            ?: error("space around assigment's rule must be a boolean")
}

object LineBreakBeforePrintlnDef : RuleDef<Int> {
    override val default: Int? = null
    override val id: String = "LineBreakBeforePrintln"


    override fun parse(json: JsonPrimitive): Int {
        val jsonInt = json.intOrNull
        if (jsonInt == null || jsonInt < 0 || jsonInt > 2) {
            error("# line breaks before println must be an integer between 0 and 2")
        }
        return jsonInt
    }
}

object LineBreakAfterSemiColonDef : RuleDef<Boolean> {
    override val default = true
    override val id: String = "LineBreakAfterSemiColon"

    

    override fun parse(json: JsonPrimitive): Boolean =
        json.booleanOrNull
            ?: error("line break after semicolon's rule must be a boolean")
}

object MaxSpaceBetweenTokensDef : RuleDef<Boolean> {
    override val default = null
    override val id: String = "MaxSpaceBetweenTokens"


    override fun parse(json: JsonPrimitive): Boolean =
        json.booleanOrNull
            ?: error("one space max between token's rule must be a boolean")
}

object IndentationDef : RuleDef<Int> {
    override val default = 2
    override val id: String = "Indentation"

    

    override fun parse(json: JsonPrimitive): Int =
        json.intOrNull
            ?: error("# indentation spaces must be an integer")
}

object InlineBraceIfStatementIdDef : RuleDef<Boolean> {
    override val default = null
    override val id: String = "InlineBraceIfStatement"

    

    override fun parse(json: JsonPrimitive): Boolean =
        json.booleanOrNull
            ?: error("inline brace if statement's rule must be a boolean")
}

object BelowLineBraceIfStatementDef : RuleDef<Boolean> {
    override val default = null
    override val id: String = "BelowLineBraceIfStatement"

    

    override fun parse(json: JsonPrimitive): Boolean =
        json.booleanOrNull
            ?: error("below line brace if statement's rule must be a boolean")
}

object KeywordSpacingAfterDef : RuleDef<Boolean> {
    override val default: Boolean = true
    override val id: String = "KeywordSpacingAfter"

    

    override fun parse(json: JsonPrimitive): Boolean =
        json.booleanOrNull
            ?: error("keyword's rule must be a boolean")
}

object BraceLineBreakDef : RuleDef<Int> {
    override val default: Int = 1
    override val id: String = "BraceLineBreak"
    override fun parse(json: JsonPrimitive): Int {
        val jsonInt = json.intOrNull
        if (jsonInt == null || jsonInt < 0) {
            error("# line breaks is an integer greater than 0")
        }
        return jsonInt
    }

}
