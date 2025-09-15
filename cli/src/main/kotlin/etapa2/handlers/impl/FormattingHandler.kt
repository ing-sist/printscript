package etapa2.handlers.impl

import DocBuilder
import etapa2.OperationHandler
import etapa2.OperationResult
import viejos.OperationRequest

import java.io.StringReader
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

import RuleGenerator
import Lexer
import LexerTokenProvider
import LexerException

import etapa2.handlers.FileInputReader
import Result

// Imports del formatter
import Formatter
import Token
import config.FormatterStyleConfig
import etapa2.handlers.TokenStream
import rules.implementations.RuleImplementation
// import rules.implementations.BeforeRule/AfterRule si armás tu lista con tipos específicos
// import tu.DocBuilder  // el que usa tu Formatter

// ADAPTER: usamos el provider como TokenStream del formatter
class ProviderAsTokenStream(
    private val provider: LexerTokenProvider
) : TokenStream {
    override fun peek(k: Int): Token = provider.peek(k)
    override fun consume(): Token = provider.consume()
}

class FormattingHandler(
    private val rules: List<RuleImplementation>,            // inyectá tus reglas de formato
    private val defaultStyle: FormatterStyleConfig,         // estilo por defecto
    private val initialDocFactory: () -> DocBuilder         // cómo crear el DocBuilder inicial
) : OperationHandler {

    override fun run(req: OperationRequest): OperationResult {
        var errors = 0
        var warnings = 0

        // 1) READ
        val original = when (val r = FileInputReader.readFile(req.sourceFile)) {
            is Result.Success -> r.value
            is Result.Failure -> {
                errors++
                println("Error: no se pudo leer el archivo '${req.sourceFile}'.")
                return OperationResult(errors, warnings)
            }
        }

        // 2) LEX (init)
        val tokenRule = RuleGenerator.createTokenRule(req.specVersion)
        val lexer = Lexer(StringReader(original), tokenRule)
        val provider = LexerTokenProvider(lexer)
        val tokenStream = ProviderAsTokenStream(provider)    // ← adapter

        // 3) CONFIG del formatter
        val style: FormatterStyleConfig = loadStyle(req.sourceFile) ?: defaultStyle
        val formatter = Formatter(rules)
        val initial = initialDocFactory()

        // 4) FORMAT (puede lanzar LexerException si hay token ERROR al consumir)
        val outDoc = try {
            formatter.format(tokenStream, style, initial)
        } catch (e: LexerException) {
            errors++
            val t = e.token
            println("Error léxico en línea ${t.location.line}, col ${t.location.startCol}: '${t.lexeme}'")
            return OperationResult(errors, warnings)
        }

        // 5) Serializar el documento formateado
        // Ajustá a tu DocBuilder real: .build(), .render() o .toString()
        val formatted: String = outDoc.toString()

        // 6) Flags: check / write / print
        val check = (req.options["check"] as? Boolean) == true
        val write = (req.options["write"] as? Boolean) == true

        when {
            check -> {
                if (formatted != original) {
                    errors++
                    println("Archivo con diferencias de formato: ${req.sourceFile} (usá --write para aplicar)")
                }
            }
            write -> {
                val dest = Path.of(req.sourceFile)
                val tmp  = Path.of(req.sourceFile + ".tmp")
                try {
                    Files.writeString(tmp, formatted)
                    Files.move(
                        tmp, dest,
                        StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE
                    )
                } catch (t: Throwable) {
                    errors++
                    println("Error al escribir el archivo formateado: ${t.message}")
                }
            }
            else -> {
                print(formatted) // stdout
            }
        }

        return OperationResult(errors, warnings)
    }

    // Cargá estilo desde archivo si te pasan --configPath; devolvé null si falla para usar default
    private fun loadStyle(configPath: String?): FormatterStyleConfig? {
        if (configPath.isNullOrBlank()) return null
        return try {
            // TODO: parseá tu formato real (json/yaml/props) a FormatterStyleConfig
            // val txt = java.io.File(configPath).readText()
            // return parseStyle(txt)
            null
        } catch (_: Exception) {
            println("Advertencia: no se pudo cargar el estilo desde '$configPath'. Se usa el estilo por defecto.")
            null
        }
    }
}