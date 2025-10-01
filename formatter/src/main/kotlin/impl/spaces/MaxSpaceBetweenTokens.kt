import config.FormatterStyleConfig
import config.MaxSpaceBetweenTokensDef
import config.RuleDef
import config.RuleRegistry
import impl.interfaces.SpaceBeforeRule

object MaxSpaceBetweenTokens : SpaceBeforeRule {
    override val id: RuleDef<Boolean> = MaxSpaceBetweenTokensDef

    init {
        RuleRegistry.registerRule(this)
    }

    override fun spaceBefore(
        curr: Token,
        style: FormatterStyleConfig,
    ): Boolean? {
        val result: Boolean? = true
        val type = curr.type
        if(type is TokenType.Space || type is TokenType.Semicolon) return false
        return result
    }
}
