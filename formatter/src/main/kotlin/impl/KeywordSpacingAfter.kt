package impl

import Token
import TokenType
import config.FormatterStyleConfig
import config.KeywordSpacingAfterDef
import config.RuleDef
import config.RuleRegistry
import impl.interfaces.SpaceAfterRule

object KeywordSpacingAfter : SpaceAfterRule  {
    override val id: RuleDef<Boolean> = KeywordSpacingAfterDef

    init {
        RuleRegistry.registerRule(this)
    }

    override fun spaceAfter(
        curr: Token,
        style: FormatterStyleConfig,
    ): Boolean? {
        var result: Boolean? = true
        if (curr.type !is TokenType.Keyword.VariableDeclaration) result = null
        return result
    }
}
