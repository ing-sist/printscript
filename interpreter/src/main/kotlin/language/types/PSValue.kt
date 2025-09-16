package language.types

sealed interface PSValue {
    val type: PSType

    fun formatForPrint(): String
}

data class PSNumber(
    val value: Double,
) : PSValue {
    override val type: PSType = PSType.NUMBER

    override fun formatForPrint(): String =
        if (value == value.toLong().toDouble()) {
            value.toLong().toString()
        } else {
            value.toString()
        }
}

data class PSString(
    val value: String,
) : PSValue {
    override val type: PSType = PSType.STRING

    override fun formatForPrint(): String = value
}

data class PSBoolean(
    val value: Boolean,
) : PSValue {
    override val type: PSType = PSType.BOOLEAN

    override fun formatForPrint(): String = value.toString()
}
