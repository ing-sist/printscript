import etapa2.OperationHandler
import etapa2.OperationResult
import etapa2.handlers.impl.ValidationCore
import etapa2.handlers.impl.ValidationOutcome
import viejos.OperationRequest

//package etapa2.handlers.impl
//
//import Diagnostic
//import Location
//
//import etapa1.Summary
//import etapa2.OperationHandler
//import etapa2.OperationResult
//import utils.Type
//import viejos.OperationRequest
//
//// Tus clases/utilidades existentes
//import parser.Parser
//import validators.provider.DefaultValidatorsProvider // si tu Parser lo requiere hoy
//import RuleGenerator
//import Lexer
//import LexerTokenProvider
//import LexerException
//
//// Helpers que ya mostraste/tenés
//import etapa2.handlers.FileInputReader
//import etapa2.handlers.Diags
//import etapa2.handlers.SemanticsRunner
//import ps.runtime.core.InterpreterRuntime
//
//
//// ---- Contrato mínimo para ejecutar el AST ----
//// Si ya tenés un intérprete/ejecutor, usalo en vez de esta interfaz:
//interface Executor {
//    fun onRuntimeDiagnostic(cb: (Diagnostic) -> Unit)      // para errores/warnings de runtime
//    fun run(ast: Any)                                       // ejecuta el programa
//}
//
//class ExecutionHandler(
//    private val interpreter: InterpreterRuntime                          // inyectás tu intérprete real aquí
//) : OperationHandler {
//
//    override fun run(req: OperationRequest): OperationResult {
//        var errors = 0
//        var warnings = 0
//
//        // -------- READ --------
//        req.progress.stageStart("read", null)
//        val content = when (val r = FileInputReader.readFile(req.sourceFile)) {
//            is Result.Success -> r.value
//            is Result.Failure -> {
//                errors++
//                req.report.emit(Diags.fileNotFound(req.sourceFile))
//                req.progress.stageEnd("read")
//                req.report.end(Summary("Execution", req.specVersion, 0, errors, warnings, 0))
//                return OperationResult(errors, warnings)
//            }
//        }
//        req.progress.stageAdvance("read", content.length.toLong())
//        req.progress.stageEnd("read")
//
//        // -------- LEX (init) --------
//        // Usamos StringReader(content) para NO reabrir el archivo
//        req.progress.stageStart("lex", null)
//        val tokenRule = RuleGenerator.createTokenRule(req.specVersion)
//        val lexer = Lexer(java.io.StringReader(content), tokenRule)
//        val provider = LexerTokenProvider(lexer)
//        req.progress.stageEnd("lex")
//
//        // -------- PARSE --------
//        req.progress.stageStart("parse", null)
//        val parser = Parser(DefaultValidatorsProvider()) // si tu Parser hoy lo requiere
//        // Si tu Parser NO tiene callbacks de sintaxis/progreso, omití esto.
//        // (De lo contrario, podrías sumar onSyntaxError/onProgress aquí)
//
//        val ast: Any = try {
//            parser.parse(provider)   // aquí pueden saltar errores léxicos al consumir tokens
//        } catch (e: LexerException) {
//            errors++
//            val tok = e.token
//            req.report.emit(
//                Diagnostic(
//                    ruleId = "Lex.Error",
//                    message = e.message ?: "Error léxico",
//                    location = Location(
//                        // usa los campos REALES de tu Location
//                        line = tok.location.line,
//                        startCol = tok.location.startCol,
//                        endCol = tok.location.endCol
//                    ),
//                    type = Type.ERROR
//                )
//            )
//            req.progress.stageEnd("parse")
//            req.report.end(Summary("Execution", req.specVersion, 0, errors, warnings, 0))
//            return OperationResult(errors, warnings)
//        }
//        req.progress.stageEnd("parse")
//
//        // -------- SEMANTICS --------
//        // IMPORTANTE: necesitamos contar los diagnósticos que emite SemanticsRunner
//        // Si tu SemanticsRunner tiene una variante con callback, usala para sumar:
//        //   SemanticsRunner.check(ast, req.specVersion, req.report, req.progress as ProgressSink) { d ->
//        //       if (d.type == Type.ERROR) errors++ else if (d.type == Type.WARNING) warnings++
//        //   }
//        // Si NO tiene callback, hacé que devuelva Pair<Int,Int> (errores, warnings) y sumalos.
//        SemanticsRunner.check(
//            ast,
//            req.specVersion,
//            req.report,
//            req.progress, )
////        ) { d ->
////            if (d.type == Type.ERROR) errors++ else if (d.type == Type.WARNING) warnings++
////        }
//
//        // Si hubo errores de validación (léxicos/sintácticos/semánticos), NO ejecutar
//        if (errors > 0) {
//            req.report.end(Summary("Execution", req.specVersion, 0, errors, warnings, 0))
//            return OperationResult(errors, warnings)
//        }
//
//        // -------- EXECUTION --------
//        req.progress.stageStart("execution", null)
//        interpreter.execute(ast)
////        interpreter.execute() { d ->
////            if (d.type == Type.ERROR) errors++ else if (d.type == Type.WARNING) warnings++
////            req.report.emit(d)
////        }
//        req.progress.stageEnd("execution")
//
//        // -------- CLOSE --------
//        req.report.end(Summary("Execution", req.specVersion, 1, errors, warnings, 0))
//        return OperationResult(errors, warnings)
//    }
//}

class ExecutionHandler(
    private val interpreter: ps.runtime.core.InterpreterRuntime
) : OperationHandler {

    override fun run(req: OperationRequest): OperationResult {
        return when (val out = ValidationCore.run(req.sourceFile, req.specVersion)) {

            is ValidationOutcome.Failure -> {
                // no hay AST
                OperationResult(out.errors, out.warnings)
            }

            is ValidationOutcome.Success -> {
                var errors = out.errors
                var warnings = out.warnings

                when (val exec = interpreter.execute(out.ast)) {
                    is Result.Success -> { }
                    is Result.Failure -> {
                        errors++
                        val ex = exec.error
                        println("Error de ejecución: ${ex.message}")
                    }
                }
                OperationResult(errors, warnings)
            }
        }
    }
}