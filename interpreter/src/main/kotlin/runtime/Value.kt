package runtime

sealed interface Value {
    fun stringify(): String
}

data class Number(
    val value: Double,
) : Value {
    override fun stringify(): String =
        if (value == value.toLong().toDouble()) {
            value.toLong().toString()
        } else {
            value.toString()
        }
}

data class Str(
    val value: String,
) : Value {
    override fun stringify(): String = value
}

data class Bool(
    val value: Boolean,
) : Value {
    override fun stringify(): String = value.toString()
}

object Nil : Value {
    override fun stringify(): String = "nil"
}
