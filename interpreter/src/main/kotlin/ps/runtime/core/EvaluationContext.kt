package ps.runtime.core

import ps.lang.types.PSType
import ps.lang.types.TypeRules
import ps.runtime.providers.EnvProvider
import ps.runtime.providers.InputProvider
import ps.runtime.providers.OutputSink

data class EvaluationContext(
    val variableStore: ScopedVariableStore,
    val inputProvider: InputProvider,
    val envProvider: EnvProvider,
    val outputSink: OutputSink,
    val typeRules: TypeRules,
    val expectedType: PSType? = null,
)
