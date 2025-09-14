import simple.SimpleArgConfig
import simple.SimpleArgDef
import utils.RuleOwner
import utils.Type

object PrintlnSimpleArgDef : SimpleArgDef {
    override val description = "println must receive an identifier or a literal (no expressions)"
    override val owner: RuleOwner = RuleOwner.USER
    override val default: SimpleArgConfig = SimpleArgConfig(true)
    override val id = "printlnSimpleArg"
    override val restrictedCases = setOf("println")
    override val type: Type = Type.ERROR

    override fun parse(configMap: Map<String, String>): SimpleArgConfig {
        val input: String? = configMap["printlnSimpleArg"] ?: return default
        return when (input) {
            "true" -> SimpleArgConfig(true)
            "false" -> SimpleArgConfig(false)
            else -> default
        }
    }
}
