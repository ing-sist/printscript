import etapa1.Summary
import etapa2.OperationResult
import utils.Type
import viejos.OperationRequest

class FormattingHandler(
    private val readLexParse: ReadLexParseHelper,
    private val formatter: Formatter
) : OperationHandler {

    override fun run(req: OperationRequest): OperationResult {
        var errors = 0
        var warnings = 0

        val parsed = readLexParse.run(req) ?: run {
            req.report.end(Summary("Formatting", req.specVersion, 0, 1, 0, 0))
            return OperationResult(1, 0)
        }
        errors += parsed.errors
        warnings += parsed.warnings

        // FORMATEO
        req.progress.stageStart("format")
        val configPath = req.options["configPath"] as? String
        val formatted = formatter.format(parsed.ast, configPath)
        req.progress.stageEnd("format")

        val check = (req.options["check"] as? Boolean) == true
        val write = (req.options["write"] as? Boolean) == true

        when {
            check -> {
                if (formatted != parsed.content) {
                    errors++
                    req.report.emit(
                        Diagnostic("Format.Diff",
                            "El archivo no respeta el formato configurado (usar --write para aplicar).",
                            Location(req.sourceFile,1,1), Type.ERROR
                        )
                    )
                }
            }
            write -> {
                // swap atómico
                val path = java.nio.file.Path.of(req.sourceFile)
                val tmp = java.nio.file.Path.of(req.sourceFile + ".tmp")
                java.nio.file.Files.writeString(tmp, formatted)
                java.nio.file.Files.move(
                    tmp, path,
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING,
                    java.nio.file.StandardCopyOption.ATOMIC_MOVE
                )
            }
            else -> {
                // si no hay flags, imprimí el resultado a stdout (no por reportSink)
                kotlin.io.print(formatted)
            }
        }

        req.report.end(Summary("Formatting", req.specVersion, 1, errors, warnings, 0))
        return OperationResult(errors, warnings)
    }
}