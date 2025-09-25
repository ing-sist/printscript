package impl

import Token
import TokenType
import config.FormatterStyleConfig
import config.RuleDef
import config.RuleRegistry
import config.SpaceAroundAssignmentDef
import impl.interfaces.SpaceAfterRule
import impl.interfaces.SpaceBeforeRule
import java.awt.desktop.AppReopenedEvent

object SpaceAroundAssignment : SpaceBeforeRule, SpaceAfterRule {
    override val id: RuleDef<Boolean> = SpaceAroundAssignmentDef

    init {
        RuleRegistry.registerRule(this)
    }

    override fun spaceBefore(
        curr: Token,
        style: FormatterStyleConfig,
    ): Boolean? {
        var result: Boolean? = true

        if(curr.type != TokenType.Assignment){
            result = null
        } else {
            if(style[SpaceAroundAssignmentDef] == false) result = false
        }
        return result
    }

    override fun spaceAfter(
        curr: Token,
        style: FormatterStyleConfig,
    ): Boolean? {
        var result: Boolean? = true
        if (curr.type !is TokenType.Assignment) return null
        if(style[SpaceAroundAssignmentDef] == false) result = false
        return result
    }
}
