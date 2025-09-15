package etapa2.handlers

import Diagnostic
import LexerTokenProvider
import etapa1.ProgressSink
import etapa1.ReportSink

object ParserRunner {
    /**
     * Parsea y emite errores sintácticos/progreso al vuelo.
     * Devuelve el AST (usá tu tipo si lo tenés) o Failure si el parser informó fallo fatal.
     */
    fun parse(
        provider: LexerTokenProvider,
        report: ReportSink,
        progress: ProgressSink // si tenés una interfaz ProgressSink, usala
    ): Result<Any, Diagnostic> { // reemplazá Any por tu AST si corresponde
        progress.stageStart("parse", null)
        val parser = parser.Parser() // tu Parser real
        parser.onSyntaxError { d -> report.emit(d) }
        parser.onProgress { consumed -> progress.stageAdvance("parse", consumed.toLong()) }
        val result = parser.parse(provider)  // asumimos que devuelve Result<AST, Diagnostic>
        progress.stageEnd("parse")
        return result
    }
}