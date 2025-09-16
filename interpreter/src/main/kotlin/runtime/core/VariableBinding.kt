package runtime.core

import language.types.PSType
import language.types.PSValue

data class VariableBinding(
    val name: String,
    val type: PSType,
    val value: PSValue,
    val isMutable: Boolean,
)
