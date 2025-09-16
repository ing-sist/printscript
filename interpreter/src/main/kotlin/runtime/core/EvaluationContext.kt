package runtime.core

import language.types.PSType
import language.types.TypeRules
import runtime.providers.EnvProvider
import runtime.providers.InputProvider
import runtime.providers.OutputSink

data class EvaluationContext(
    val variableStore: ScopedVariableStore,
    val inputProvider: InputProvider,
    val envProvider: EnvProvider,
    val outputSink: OutputSink,
    val typeRules: TypeRules,
    val expectedType: PSType? = null,
)
