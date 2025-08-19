class Ast (value: String) {
    private val astValue: String = value

    fun getValue(): String {
        return astValue
    }

    override fun toString(): String {
        return "Ast(value='$astValue')"
    }
}