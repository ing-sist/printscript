//package impl
//
//import Token
//import config.FormatterStyleConfig
//import config.MaxSpaceBetweenTokensDef
//import config.RuleDef
//import config.RuleRegistry
//import impl.interfaces.SpaceBeforeRule
//
//object MaxSpaceBetweenTokens : SpaceBeforeRule {
//    override val id: RuleDef<Boolean> = MaxSpaceBetweenTokensDef
//
//    init {
//        RuleRegistry.registerRule(this)
//    }
//
//    override fun spaceBefore(
//        prev: Token,
//        curr: Token,
//        style: FormatterStyleConfig,
//    ): Boolean? {
//        var result: Boolean? = null
//        if(style[MaxSpaceBetweenTokensDef] == false) return false
//        if (prev.type is TokenType.Space && curr.type is TokenType.Space) result = null
//
//        return result
//    }
//}
