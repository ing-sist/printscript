package etapa2.shared

import java.io.File
import java.io.FileReader

import Lexer
import LexerTokenProvider
import RuleGenerator

import Diagnostic
import Location
import etapa1.ProgressSink
import etapa1.ReportSink
import utils.Type

data class TokenStream(
    val content: String,
    val provider: LexerTokenProvider
)

object TokenStreamBuilder {
    fun build(
        sourceFile: String,
        specVersion: String,
        report: ReportSink,
        progress: ProgressSink // o tu interfaz ProgressSink
    ): TokenStream? {
        // READ
        progress.stageStart("read", null)
        val file = File(sourceFile)
        if (!file.exists() || !file.isFile) {
            report.emit(
                Diagnostic(
                    ruleId = "CLI.FileNotFound",
                    message = "No se pudo leer el archivo '$sourceFile'.",
                    location = Location(sourceFile, 1, 1, 1, 1),
                    type = Type.ERROR
                )
            )
            progress.stageEnd("read")
            return null
        }

        val content = try {
            file.readText()
        } catch (e: Exception) {
            report.emit(
                Diagnostic(
                    ruleId = "CLI.FileReadError",
                    message = "Error al leer '$sourceFile': ${e.message}",
                    location = Location(sourceFile, 1, 1, 1, 1),
                    type = Type.ERROR
                )
            )
            progress.stageEnd("read")
            return null
        }
        progress.stageAdvance("read", content.length.toLong())
        progress.stageEnd("read")

        // LEX (configurado por versión)
        progress.stageStart("lex", null)
        val tokenRule = RuleGenerator.createTokenRule(specVersion)
        val reader = FileReader(file) // si tu Lexer necesita Reader
        val lexer = Lexer(reader, tokenRule)
        val tokenProvider = LexerTokenProvider(lexer)
        progress.stageEnd("lex")

        return TokenStream(
            content = content,
            provider = tokenProvider
        )
    }
}