import simple.SimpleArgDef

object ReadInputSimpleArgDef : SimpleArgDef {
    override val id = "ReadInput.SimpleArg"
    override val description = "Read Input must receive an identifier or a literal (no expressions)"
    override val restrictedCases = setOf("readInput")
}
