class PrintScriptEngine {
    private var version: String = "1.0"
    private var analyzerConfigPath: String? = null

    fun setVersion(v: String) {
        require(RuleGenerator.isVersionSupported(v)) { "Unsupported version: $v" }
        version = v
    }

    fun setAnalyzerConfig(path: String?) {
        analyzerConfigPath = path
    }

    fun countTokens(path: String): Int {
        val lexer =
            Lexer(
                java.io.FileReader(
                    java.io.File(path),
                ),
                RuleGenerator.createTokenRule(version),
            )
        val ts = LexerTokenProvider(lexer, readSpace = false, readNewline = false)
        var i = 0
        while (true) {
            val t = ts.peek(i)
            if (t.type is TokenType.EOF) break
            i++
        }
        return i
    }

    fun validateSyntax(path: String) {
        val lexer =
            Lexer(
                java.io.FileReader(
                    java.io.File(path),
                ),
                RuleGenerator.createTokenRule(version),
            )
        val ts = LexerTokenProvider(lexer, readSpace = false, readNewline = false)
        val parser = parser.Parser(validators.provider.DefaultValidatorsProvider())
        while (true) {
            val next = ts.peek(0)
            if (next.type is TokenType.EOF) break
            when (val res = parser.parse(ts)) {
                is Result.Success -> { /* continue to next statement */ }
                is Result.Failure -> error("Parse error: ${res.error}")
            }
        }
    }

    fun parseAst(path: String): AstNode {
        val lexer =
            Lexer(
                java.io.FileReader(
                    java.io.File(path),
                ),
                RuleGenerator.createTokenRule(version),
            )
        val ts = LexerTokenProvider(lexer, readSpace = false, readNewline = false)
        val parser = parser.Parser(validators.provider.DefaultValidatorsProvider())
        return when (val res = parser.parse(ts)) {
            is Result.Success -> res.value
            is Result.Failure -> error("Parse error: ${res.error}")
        }
    }

    fun execute(
        path: String,
        inputs: List<String> = emptyList(),
        env: Map<String, String> = emptyMap(),
    ): String {
        // Build token stream and parser to iterate all statements
        val lexer = Lexer(java.io.FileReader(java.io.File(path)), RuleGenerator.createTokenRule(version))
        val ts = LexerTokenProvider(lexer, readSpace = false, readNewline = false)
        val parser = parser.Parser(validators.provider.DefaultValidatorsProvider())

        // Single runtime for the whole program
        val inputProvider = runtime.providers.ProgrammaticInputProvider(inputs.toMutableList())
        val envProvider = runtime.providers.MapEnvProvider(env)
        val outputSink = runtime.providers.BufferedOutputSink()
        val runtime =
            runtime.core
                .InterpreterRuntimeFactory()
                .createRuntime(inputProvider, envProvider, outputSink)

        while (true) {
            val next = ts.peek(0)
            if (next.type is TokenType.EOF) break
            when (val res = parser.parse(ts)) {
                is Result.Success -> {
                    when (val execRes = runtime.execute(res.value)) {
                        is Result.Failure -> throw execRes.error
                        is Result.Success -> {}
                    }
                }
                is Result.Failure -> error("Parse error: ${res.error}")
            }
        }
        return outputSink.getJoinedOutput()
    }

    fun analyzeAll(path: String): Report {
        val lexer =
            Lexer(
                java.io.FileReader(
                    java.io.File(path),
                ),
                RuleGenerator.createTokenRule(version),
            )
        val ts = LexerTokenProvider(lexer, readSpace = false, readNewline = false)
        val parser = parser.Parser(validators.provider.DefaultValidatorsProvider())

        val cfg =
            analyzerConfigPath?.let {
                AnalyzerConfig.fromPath(
                    it,
                    shared.AnalyzerRuleDefinitions.RULES,
                )
            } ?: AnalyzerConfig(
                mapOf(),
            )
        val rules =
            listOf(
                naming.IdentifierNamingRule(naming.IdentifierNamingRuleDef),
                simple.SimpleArgRule(PrintlnSimpleArgDef),
                simple.SimpleArgRule(ReadInputSimpleArgDef),
            )
        val analyzer = Analyzer(rules)
        val report = Report.inMemory()

        while (true) {
            val next = ts.peek(0)
            if (next.type is TokenType.EOF) break
            when (val res = parser.parse(ts)) {
                is Result.Success -> analyzer.analyze(res.value, report, cfg)
                is Result.Failure -> error("Parse error: ${res.error}")
            }
        }
        return report
    }

    fun analyze(path: String): Report = analyzeAll(path)
}
