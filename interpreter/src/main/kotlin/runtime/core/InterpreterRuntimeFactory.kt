package runtime.core

import AssignmentNode
import BinaryOperationNode
import ConditionalNode
import DeclarationAssignmentNode
import DeclarationNode
import FunctionCallNode
import IdentifierNode
import LiteralNode
import interpret.expression.BinaryOperationEvaluator
import interpret.expression.FunctionCallDispatcher
import interpret.expression.LiteralExpressionEvaluator
import interpret.expression.VariableReferenceEvaluator
import interpret.registry.HandlerRegistry
import interpret.statements.AssignmentExecutor
import interpret.statements.ConditionalExecutor
import interpret.statements.DeclarationAssignmentExecutor
import interpret.statements.DeclarationExecutor
import interpret.statements.FunctionCallExecutor
import language.types.DefaultTypeRules
import language.types.TypeRules
import runtime.providers.ConsoleOutputSink
import runtime.providers.EnvProvider
import runtime.providers.InputProvider
import runtime.providers.OutputSink
import runtime.providers.StdinInputProvider
import runtime.providers.SystemEnvProvider

class InterpreterRuntimeFactory {
    fun createRuntime(
        inputProvider: InputProvider = StdinInputProvider(),
        envProvider: EnvProvider = SystemEnvProvider(),
        outputSink: OutputSink = ConsoleOutputSink(),
        typeRules: TypeRules = DefaultTypeRules(),
    ): Interpreter {
        val variableStore = ScopedVariableStore()
        val evaluationContext =
            EvaluationContext(
                variableStore,
                inputProvider,
                envProvider,
                outputSink,
                typeRules,
            )

        val expressionRegistry = HandlerRegistry()
        val statementRegistry = HandlerRegistry()

        val runtime =
            InterpreterRuntime(
                evaluationContext,
                expressionRegistry,
                statementRegistry,
            )

        registerAllHandlers(runtime, expressionRegistry, statementRegistry)

        return runtime
    }

    private fun registerAllHandlers(
        runtime: InterpreterRuntime,
        expressionRegistry: HandlerRegistry,
        statementRegistry: HandlerRegistry,
    ) {
        // Registrar evaluadores de expresiones
        expressionRegistry.registerExpressionEvaluator(
            LiteralNode::class.java,
            LiteralExpressionEvaluator(),
        )

        expressionRegistry.registerExpressionEvaluator(
            IdentifierNode::class.java,
            VariableReferenceEvaluator(),
        )

        expressionRegistry.registerExpressionEvaluator(
            BinaryOperationNode::class.java,
            BinaryOperationEvaluator(runtime),
        )

        expressionRegistry.registerExpressionEvaluator(
            FunctionCallNode::class.java,
            FunctionCallDispatcher(runtime),
        )

        // Registrar ejecutores de declaraciones
        statementRegistry.registerStatementExecutor(
            DeclarationAssignmentNode::class.java,
            DeclarationAssignmentExecutor(runtime),
        )

        statementRegistry.registerStatementExecutor(
            DeclarationNode::class.java,
            DeclarationExecutor(),
        )
        statementRegistry.registerStatementExecutor(
            AssignmentNode::class.java,
            AssignmentExecutor(runtime),
        )

        statementRegistry.registerStatementExecutor(
            ConditionalNode::class.java,
            ConditionalExecutor(runtime),
        )

        statementRegistry.registerStatementExecutor(
            FunctionCallNode::class.java,
            FunctionCallExecutor(runtime),
        )
    }
}
