package etapa2.handlers.impl

import Lexer
import LexerException
import LexerTokenProvider
import etapa2.handlers.FileInputReader
import parser.Parser
import validators.provider.DefaultValidatorsProvider

object ValidationCore {
    data class Outcome(
        val ast: Any?,       // reemplazá Any por tu tipo de AST (p.ej., AstNode)
        val errors: Int,
        val warnings: Int
    )

    fun run(sourceFile: String, specVersion: String): Outcome {
        var errors = 0
        var warnings = 0

        // READ
        val content = when (val r = FileInputReader.readFile(sourceFile)) {
            is Result.Success -> r.value
            is Result.Failure -> {
                errors++
                println("Error: no se pudo leer el archivo '$sourceFile'.")
                return Outcome(null, errors, warnings)
            }
        }

        // LEX (init)
        val tokenRule = RuleGenerator.createTokenRule(specVersion)
        val lexer = Lexer(java.io.StringReader(content), tokenRule)
        val provider = LexerTokenProvider(lexer)

        // PARSE
        val parser = Parser(DefaultValidatorsProvider()) // si tu Parser hoy lo requiere
        val ast = try {
            parser.parse(provider) // tipá a tu AST real si es necesario
        } catch (e: LexerException) {
            errors++
            val tok = e.token
            println("Error léxico en línea ${tok.location.line}, col ${tok.location.startCol}: '${tok.lexeme}'")
            return Outcome(null, errors, warnings)
        }

        // SEMÁNTICA (opcional, “mínimo” sin sinks)
        // Si ya tenés una función que devuelve conteos:
        // val (semE, semW) = SemanticsLite.check(ast, specVersion)
        // errors += semE; warnings += semW

        return Outcome(ast, errors, warnings)
    }
}