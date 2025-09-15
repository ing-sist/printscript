package etapa2.handlers

import Lexer
import LexerTokenProvider
import etapa1.CliFileError

object TokenStream {
    fun build(filePath: String, specVersion: String): Result<LexerTokenProvider, CliFileError> {
        val file = java.io.File(filePath)
        if (!file.exists() || !file.isFile) return Result.Failure(CliFileError(filePath))
        val tokenRule = RuleGenerator.createTokenRule(specVersion)
        val reader = java.io.FileReader(file)
        val lexer = Lexer(reader, tokenRule)
        return Result.Success(LexerTokenProvider(lexer))
    }
}