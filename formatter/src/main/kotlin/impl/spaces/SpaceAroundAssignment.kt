package impl.spaces

import Token
import TokenType
import config.FormatterStyleConfig
import config.RuleDef
import config.RuleRegistry
import config.SpaceAroundAssignmentDef
import impl.interfaces.SpaceAfterRule
import impl.interfaces.SpaceBeforeRule

object SpaceAroundAssignment : SpaceBeforeRule, SpaceAfterRule {
    override val id: RuleDef<Boolean> = SpaceAroundAssignmentDef

    init {
        RuleRegistry.registerRule(this)
    }

    override fun spaceBefore(
        curr: Token,
        style: FormatterStyleConfig,
    ): Boolean? {
        if (curr.type !is TokenType.Assignment) return null
        return style[SpaceAroundAssignmentDef]
    }

    override fun spaceAfter(
        curr: Token,
        style: FormatterStyleConfig,
    ): Boolean? {
        if (curr.type !is TokenType.Assignment) return null
        return style[SpaceAroundAssignmentDef]
    }
}
