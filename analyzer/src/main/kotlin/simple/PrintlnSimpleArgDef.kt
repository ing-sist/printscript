import simple.SimpleArgDef

object PrintlnSimpleArgDef : SimpleArgDef {
    override val description = "println must receive an identifier or a literal (no expressions)"
    override val id = "Println.SimpleArg"
    override val restrictedCases = setOf("println")
}
