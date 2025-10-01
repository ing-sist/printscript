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
        return style[MaxSpaceBetweenTokensDef]
    }
}
