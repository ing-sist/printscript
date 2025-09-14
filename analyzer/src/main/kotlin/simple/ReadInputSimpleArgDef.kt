import simple.SimpleArgConfig
import simple.SimpleArgDef
import utils.RuleOwner
import utils.Type

object ReadInputSimpleArgDef : SimpleArgDef {
    override val id = "readInputSimpleArg"
    override val description = "Read Input must receive an identifier or a literal (no expressions)"
    override val owner: RuleOwner = RuleOwner.USER
    override val default: SimpleArgConfig = SimpleArgConfig(true)
    override val restrictedCases = setOf("readInput")
    override val type: Type = Type.ERROR

    override fun parse(configMap: Map<String, String>): SimpleArgConfig {
        val input: String? = configMap["readInputSimpleArg"] ?: return default
        return when (input) {
            "true" -> SimpleArgConfig(true)
            "false" -> SimpleArgConfig(false)
            else -> default
        }
    }
}
