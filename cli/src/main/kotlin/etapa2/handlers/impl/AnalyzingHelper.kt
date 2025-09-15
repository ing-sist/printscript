package etapa2.handlers.impl


import java.io.StringReader

import etapa2.handlers.FileInputReader
import Result

import RuleGenerator
import Lexer
import LexerTokenProvider
import LexerException

import parser.Parser
import validators.provider.DefaultValidatorsProvider

// Ajustá estos imports a tus tipos reales
import Diagnostic
import utils.Type

object AnalyzingHelper {
    data class Outcome(
        val errors: Int,
        val warnings: Int
    )

    /**
     * Core de análisis sin report/progress.
     * - Lee el archivo
     * - Inicializa lexer + token stream
     * - Parsea a AST
     * - Ejecuta la lambda `analyze(ast)` que debe devolver una lista de Diagnostic
     * - Imprime diagnósticos y devuelve contadores
     */
    fun run(
        sourceFile: String,
        specVersion: String,
        analyze: (ast: Any) -> List<Diagnostic>   // cambiá Any por tu AST real si querés
    ): Outcome {
        var errors = 0
        var warnings = 0

        // READ
        val content = when (val r = FileInputReader.readFile(sourceFile)) {
            is Result.Success -> r.value
            is Result.Failure -> {
                errors++
                println("Error: no se pudo leer el archivo '$sourceFile'.")
                return Outcome(errors, warnings)
            }
        }

        // LEX (init)
        val tokenRule = RuleGenerator.createTokenRule(specVersion)
        val lexer = Lexer(StringReader(content), tokenRule)
        val provider = LexerTokenProvider(lexer)

        // PARSE
        val parser = Parser(DefaultValidatorsProvider()) // si hoy tu Parser lo requiere
        val ast = try {
            parser.parse(provider)  // tipá si tu parse() retorna AstNode
        } catch (e: LexerException) {
            errors++
            val t = e.token
            println("Error léxico en línea ${t.location.line}, col ${t.location.startCol}: '${t.lexeme}'")
            return Outcome(errors, warnings)
        }

        // ANALYZE (usa tu Analyzer a través de la lambda)
        val diags = analyze(ast)
        for (d in diags) {
            when (d.type) {
                Type.ERROR   -> { errors++;   println("Error: ${renderDiag(d)}") }
                Type.WARNING -> { warnings++; println("Warning: ${renderDiag(d)}") }
                else         -> println("Info: ${renderDiag(d)}")
            }
        }

        return Outcome(errors, warnings)
    }

    // Render simple para consola (provisorio)
    private fun renderDiag(d: Diagnostic): String {
        // Ajustá a tu estructura real de Location/Diagnostic
        val loc = d.location
        val where = if (loc != null) " (línea ${loc.line}, col ${loc.startCol})" else ""
        return "${d.ruleId}: ${d.message}$where"
    }
}