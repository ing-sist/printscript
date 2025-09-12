package rules.definitions

import rules.RuleOwner

object LineBreakBeforePrintlnDef : Rule<Int> {
    override val id: String = "LineBreakBeforePrintln"
    override val default: Int = 1
    override val owner: RuleOwner = RuleOwner.USER

    override fun parse(raw: String): Int = raw.trim().toInt() // result if > 2
}

object LineBreakAfterSemiColonDef : Rule<Boolean> {
    override val id: String = "LineBreakAfterSemiColon"
    override val default: Boolean = true
    override val owner: RuleOwner = RuleOwner.ENGINE

    override fun parse(raw: String): Boolean = raw.trim().lowercase() == "true"
}

object SpaceBeforeColonDef : Rule<Boolean> {
    override val id: String = "SpaceBeforeColonDef"
    override val default: Boolean = true
    override val owner: RuleOwner = RuleOwner.USER

    override fun parse(raw: String): Boolean = raw.trim().lowercase() == "true"
}

object SpaceAfterColonDef : Rule<Boolean> {
    override val id: String = "SpaceAfterColonDef"
    override val default: Boolean = true
    override val owner: RuleOwner = RuleOwner.USER

    override fun parse(raw: String): Boolean = raw.trim().lowercase() == "true"
}

object SpaceAroundAsignementDef : Rule<Boolean> {
    override val id: String = "SpaceAroundAsignement"
    override val default: Boolean = true
    override val owner: RuleOwner = RuleOwner.USER

    override fun parse(raw: String): Boolean = raw.trim().lowercase() == "true"
}

object SpaceBetweenTokensDef : Rule<Boolean> {
    override val id: String = "SpaceAroundAsignement"
    override val default: Boolean = true
    override val owner: RuleOwner = RuleOwner.ENGINE

    override fun parse(raw: String): Boolean = raw.trim().lowercase() == "true"
}

object SpaceAroundOperatorsDef : Rule<Boolean> {
    override val id: String = "SpaceAroundOperator"
    override val default: Boolean = true
    override val owner: RuleOwner = RuleOwner.ENGINE

    override fun parse(raw: String): Boolean = raw.trim().lowercase() == "true"
}

object IfStatementIndentationDef : Rule<Int> {
    override val id: String = "IfStatementIndentation"
    override val default: Int = 4
    override val owner: RuleOwner = RuleOwner.USER

    override fun parse(raw: String): Int = raw.trim().toInt()
}

object InlineIfBraceIfStatementDef : Rule<Boolean> {
    override val id: String = "InlineIfBrace"
    override val default: Boolean = true
    override val owner: RuleOwner = RuleOwner.ENGINE

    override fun parse(raw: String): Boolean = raw.trim().lowercase() == "true"
}
