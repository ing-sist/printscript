import progress.ProgressReporter
import java.io.File

class PrintScriptEngine {
    private var version: String = "1.0"
    private var analyzerConfigPath: String? = null
    private var formatterConfigPath: String? = null

    fun setVersion(v: String) {
        require(RuleGenerator.isVersionSupported(v)) { "Unsupported version: $v" }
        version = v
    }

    fun setAnalyzerConfig(path: String?) {
        analyzerConfigPath = path
    }

    fun setFormatterConfig(path: String?) {
        formatterConfigPath = path
    }

    private fun createParserLexer(file: File): Lexer =
        Lexer(
            file.reader(),
            RuleGenerator.createTokenRule(version),
        )

    private fun createParserTokenProvider(lexer: Lexer): LexerTokenProvider =
        LexerTokenProvider(lexer, readSpace = false, readNewline = false)

    private inline fun <T> processStatements(
        file: File,
        progressReporter: ProgressReporter,
        operationName: String,
        initialState: T,
        crossinline processStatement: (AstNode, T) -> Unit,
    ): T {
        val totalLines = file.readLines().size.coerceAtLeast(1)
        val lexer = createParserLexer(file)
        val tokenProvider = createParserTokenProvider(lexer)
        val parser = parser.Parser(validators.provider.DefaultValidatorsProvider(version))

        progressReporter.reportProgress("$operationName...", 0)

        while (true) {
            val next = tokenProvider.peek(0)
            if (next.type is TokenType.EOF) break

            val currentLine = next.location.line
            val percentage = (currentLine * 100) / totalLines
            progressReporter.reportProgress("$operationName line $currentLine...", percentage)

            when (val res = parser.parse(tokenProvider)) {
                is Result.Success -> processStatement(res.value, initialState)
                is Result.Failure -> error("Parse error: ${res.error}")
            }
        }

        progressReporter.reportSuccess("$operationName complete")
        return initialState
    }

    fun validateSyntax(
        path: String,
        progressReporter: ProgressReporter,
    ) {
        val file = File(path)
        processStatements(file, progressReporter, "Validating", Unit) { _, _ ->
            // Just parsing is enough for validation
        }
    }

    fun execute(
        path: String,
        progressReporter: ProgressReporter,
    ): String {
        val file = File(path)
        val outputSink = runtime.providers.BufferedOutputSink()
        val runtime =
            runtime.core
                .InterpreterRuntimeFactory()
                .createRuntime(outputSink = outputSink)

        processStatements(file, progressReporter, "Executing", runtime) { astNode, runtimeInstance ->
            when (val execRes = runtimeInstance.execute(astNode)) {
                is Result.Failure -> throw execRes.error
                is Result.Success -> {}
            }
        }

        return outputSink.getJoinedOutput()
    }

    fun analyze(
        path: String,
        progressReporter: ProgressReporter,
    ): Report {
        val file = File(path)

        val config =
            analyzerConfigPath?.let {
                AnalyzerConfig.fromPath(
                    it,
                    shared.AnalyzerRuleDefinitions.RULES,
                )
            } ?: AnalyzerConfig(mapOf())

        val rules =
            listOf(
                naming.IdentifierNamingRule(naming.IdentifierNamingRuleDef),
                simple.SimpleArgRule(PrintlnSimpleArgDef),
                simple.SimpleArgRule(ReadInputSimpleArgDef),
            )
        val analyzer = Analyzer(rules)
        val report = Report.inMemory()

        data class AnalyzerState(
            val analyzer: Analyzer,
            val report: Report,
            val config: AnalyzerConfig,
        )

        val state = AnalyzerState(analyzer, report, config)

        processStatements(file, progressReporter, "Analyzing", state) { astNode, analyzerState ->
            analyzerState.analyzer.analyze(astNode, analyzerState.report, analyzerState.config)
        }

        return report
    }

    fun format(
        path: String,
        progressReporter: ProgressReporter,
    ): String {
        progressReporter.reportProgress("Formatting...", 0)

        // Initialize rules registry
        config.ForceRulesInit.loadAll()

        val tokenRule = RuleGenerator.createTokenRule(version)
        val file = File(path)
        val reader = file.reader()
        val lexer = Lexer(reader, tokenRule)
        val tokenProvider = LexerTokenProvider(lexer, readSpace = true, readNewline = true)

        val styleConfig =
            formatterConfigPath?.let {
                val configText = File(it).readText()
                // Use DefaultRuleAdapter instead of custom lambda
                ConfigLoader(config.DefaultRuleAdapter).loadFromString(configText)
            } ?: config.FormatterStyleConfig(emptyMap())

        val formatter = Formatter(config.RuleRegistry.allRules())
        val docBuilder = DocBuilder.inMemory()

        val result = formatter.format(tokenProvider, styleConfig, docBuilder)
        val formattedCode = result.build()

        progressReporter.reportSuccess("Formatting complete")
        return formattedCode
    }
}
