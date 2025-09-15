package etapa2.handlers.impl

import java.io.StringReader
import AstNode
import Lexer
import LexerException
import LexerTokenProvider
import RuleGenerator
import parser.Parser
import validators.provider.DefaultValidatorsProvider
import etapa2.handlers.FileInputReader
import Result
import etapa1.CliFileError
import parser.ParseError

sealed class ValidationOutcome {
    data class Success(
        val ast: AstNode,
        val errors: Int,
        val warnings: Int
    ) : ValidationOutcome()

    data class Failure(
        val errors: Int,
        val warnings: Int,
        val cause: Exception
    ) : ValidationOutcome()
}


object ValidationCore {

    fun run(sourceFile: String, specVersion: String): ValidationOutcome {
        var errors = 0
        var warnings = 0

        // READ
        val content = when (val r = FileInputReader.readFile(sourceFile)) {
            is Result.Success -> r.value
            is Result.Failure -> {
                errors++
                println("Error: no se pudo leer el archivo '$sourceFile'.")
                return ValidationOutcome.Failure(errors, warnings, CliFileError(sourceFile))
            }
        }

        // LEX (init)
        val tokenRule = RuleGenerator.createTokenRule(specVersion)
        val lexer = Lexer(StringReader(content), tokenRule)
        val provider = LexerTokenProvider(lexer)

        // PARSE
        val parser = Parser(DefaultValidatorsProvider())
        val ast = try {
            when (val pr = parser.parse(provider)) {
                is Result.Success -> pr.value
                is Result.Failure -> {
                    errors++
                    println(renderParseError(pr.error))
                    return ValidationOutcome.Failure(errors, warnings, pr.error)
                }
            }
        } catch (e: LexerException) {
            errors++
            val tok = e.token
            println("Error léxico en línea ${tok.location.line}, col ${tok.location.startCol}: '${tok.lexeme}'")
            return ValidationOutcome.Failure(errors, warnings, ParseError.InvalidSyntax(listOf(tok), "Lexer error"))
        }

        return ValidationOutcome.Success(ast, errors, warnings)
    }

    private fun renderParseError(err: ParseError): String = when (err) {
        is ParseError.InvalidSyntax ->
            "Invalid syntax: ${err.reason} for tokens: ${err.tokenGroup.joinToString(" ") { it.lexeme }}"
        is ParseError.NoValidParser ->
            "No valid parser found for tokens: ${err.tokenGroup.joinToString(" ") { it.lexeme }}"
        is ParseError.UnexpectedToken ->
            "Unexpected token '${err.token.lexeme}' at ${err.token.location}. Expected: ${err.expected}"
    }
}