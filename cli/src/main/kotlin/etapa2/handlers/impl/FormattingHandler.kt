import config.FormatterStyleConfig
import etapa2.OperationHandler
import etapa2.OperationResult
import etapa2.handlers.FileInputReader
import rules.implementations.RuleImplementation
import viejos.OperationRequest
import java.io.StringReader

class FormattingHandler(
    private val rules: List<RuleImplementation>,
    private val defaultStyle: FormatterStyleConfig,
    private val initialDocFactory: () -> DocBuilder
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
        val tokenStream = LexerTokenProvider(lexer)

        // 3) CONFIG del formatter
        val configPath = req.sourceFile  // <-- usar opción, no sourceFile
        val style: FormatterStyleConfig = loadStyle(configPath) ?: defaultStyle
        val formatter = Formatter(rules)
        val initial = initialDocFactory()

        // 4) FORMAT
        val outDoc = try {
            formatter.format(tokenStream, style, initial)
        } catch (e: LexerException) {
            errors++
            val t = e.token
            println("Error léxico en línea ${t.location.line}, col ${t.location.startCol}: '${t.lexeme}'")
            return OperationResult(errors, warnings)
        }

        // 5) Render final (ajustá si tu DocBuilder tiene build()/render())
        val formatted: String = outDoc.toString()

        // 6) Flags
//        val check = (req.options["check"] as? Boolean) == true
//        val write = (req.options["write"] as? Boolean) == true
//
//        when {
//            check -> {
//                if (formatted != original) {
//                    errors++
//                    println("Archivo con diferencias de formato: ${req.sourceFile} (usá --write para aplicar)")
//                }
//            }
//            write -> {
//                val dest = Path.of(req.sourceFile)
//                val tmp  = Path.of(req.sourceFile + ".tmp")
//                try {
//                    Files.writeString(tmp, formatted)
//                    try {
//                        Files.move(tmp, dest, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE)
//                    } catch (_: Exception) {
//                        // fallback si ATOMIC_MOVE no está soportado
//                        Files.move(tmp, dest, StandardCopyOption.REPLACE_EXISTING)
//                    }
//                } catch (t: Throwable) {
//                    errors++
//                    println("Error al escribir el archivo formateado: ${t.message}")
//                }
//            }
//            else -> {
//                print(formatted)
//            }
//        }

        return OperationResult(errors, warnings)
    }

    private fun loadStyle(configPath: String?): FormatterStyleConfig? {
        if (configPath.isNullOrBlank()) return null
        return try {
            // TODO: parseá el archivo de estilo real a FormatterStyleConfig
            // val txt = java.io.File(configPath).readText()
            // parseStyle(txt)
            null
        } catch (_: Exception) {
            println("Advertencia: no se pudo cargar el estilo desde '$configPath'. Se usa el estilo por defecto.")
            null
        }
    }
}