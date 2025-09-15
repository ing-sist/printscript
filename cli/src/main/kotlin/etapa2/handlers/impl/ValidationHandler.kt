import etapa2.OperationHandler
import etapa2.OperationResult
import etapa2.handlers.impl.ValidationCore
import viejos.OperationRequest

//package etapa2.handlers.impl
//
//import Diagnostic
//import Lexer
//import LexerException
//import LexerTokenProvider
//import Location
//import etapa1.ProgressSink
//import etapa1.Summary
//import etapa2.OperationHandler
//import etapa2.OperationResult
//import etapa2.handlers.Diags
//import etapa2.handlers.FileInputReader
//import etapa2.handlers.SemanticsRunner
//import parser.Parser
//import utils.Type
//import validators.provider.DefaultValidatorsProvider
//import viejos.OperationRequest
//import java.io.File
//import java.io.FileReader
//
//class ValidationHandler : OperationHandler {
//    override fun run(req: OperationRequest): OperationResult {
//        var errors = 0
//        var warnings = 0
//
//        // READ
//        // aviso que voy a empezar a leer el file
//        req.progress.stageStart("read", null)
//        // leo el archivo y devuelvo un result
//        val content = when (val r = FileInputReader.readFile(req.sourceFile)) {
//            is Result.Success -> r.value
//            is Result.Failure -> {
//                errors++ // sumo error + emito que termine + reporto error
//                req.report.emit(Diags.fileNotFound(req.sourceFile))
//                req.progress.stageEnd("read")
//                req.report.end(Summary("Validation", req.specVersion, 0, errors, warnings, 0))
//                return OperationResult(errors, warnings)
//            }
//        }
//        req.progress.stageAdvance("read", content.length.toLong())
//        req.progress.stageEnd("read")
//
//        // LEXER + TOKEN PROVIDER
//        req.progress.stageStart("lex", null)
//        val tokenRule = RuleGenerator.createTokenRule(req.specVersion)
//        val reader = FileReader(File(req.sourceFile))
//        val lexer = Lexer(reader, tokenRule)
//        val provider = LexerTokenProvider(lexer)
//        req.progress.stageEnd("lex")
//
//        req.progress.stageStart("parse", null)
//        val parser = Parser(DefaultValidatorsProvider())
//
////        parser.onSyntaxError { d ->
////            if (d.type == Type.ERROR) errors++ else if (d.type == Type.WARNING) warnings++
////            req.report.emit(d)
////        }
////        parser.onProgress { n -> req.progress.stageAdvance("parse", n.toLong()) }
//
//        val ast = try {
//            parser.parse(provider)   // aquí se disparan peek/consume y puede saltar LexerException
//        } catch (e: LexerException) {
//            errors++
//            val tok = e.token
//            req.report.emit(
//                Diagnostic(
//                    ruleId = "Lex.Error",
//                    message = e.message ?: "Error léxico",
//                    location = Location(
//                        line = tok.location.line,
//                        startCol  = tok.location.startCol,
//                        endCol    = tok.location.endCol,
//                    ),
//                    type = Type.ERROR,
//                )
//            )
//            req.progress.stageEnd("parse")
//            req.report.end(Summary("Validation", req.specVersion, 0, errors, warnings, 0))
//            return OperationResult(errors, warnings)
//        }
//        req.progress.stageEnd("parse")
//
//
//        // SEMÁNTICA (streaming de diagnósticos)
//        SemanticsRunner.check(ast, req.specVersion, req.report, req.progress as ProgressSink)
//
//        // Cierre
//        req.report.end(Summary("Validation", req.specVersion, 1, errors, warnings, 0))
//        return OperationResult(errors, warnings)
//    }
//}

class ValidationHandler : OperationHandler {
    override fun run(req: OperationRequest): OperationResult {
        val out = ValidationCore.run(req.sourceFile, req.specVersion)
        // Podrías agregar semántica aquí si querés que validación incluya sí o sí esa etapa
        return OperationResult(errors = out.errors, warnings = out.warnings)
    }
}