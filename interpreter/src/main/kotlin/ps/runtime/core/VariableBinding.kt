package ps.runtime.core

import ps.lang.types.PSType
import ps.lang.types.PSValue

data class VariableBinding(
    val name: String,
    val type: PSType,
    val value: PSValue,
    val isMutable: Boolean,
)
