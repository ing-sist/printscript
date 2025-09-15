package ps.runtime.core

import AssignmentNode
import BinaryOperationNode
import ConditionalNode
import DeclarationAssignmentNode
import DeclarationNode
import FunctionCallNode
import IdentifierNode
import LiteralNode
import ps.interpret.expr.BinaryOperationEvaluator
import ps.interpret.expr.FunctionCallDispatcher
import ps.interpret.expr.LiteralExpressionEvaluator
import ps.interpret.expr.VariableReferenceEvaluator
import ps.interpret.registry.HandlerRegistry
import ps.interpret.stmt.AssignmentExecutor
import ps.interpret.stmt.ConditionalExecutor
import ps.interpret.stmt.DeclarationAssignmentExecutor
import ps.interpret.stmt.DeclarationExecutor
import ps.interpret.stmt.FunctionCallExecutor
import ps.lang.types.DefaultTypeRules
import ps.lang.types.TypeRules
import ps.runtime.providers.ConsoleOutputSink
import ps.runtime.providers.EnvProvider
import ps.runtime.providers.InputProvider
import ps.runtime.providers.OutputSink
import ps.runtime.providers.StdinInputProvider
import ps.runtime.providers.SystemEnvProvider

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
