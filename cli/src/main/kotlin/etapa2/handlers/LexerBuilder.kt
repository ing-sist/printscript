package etapa2.handlers

import Lexer
import LexerTokenProvider
import java.io.File
import java.io.FileReader

object LexerBuilder {
    fun build(filePath: String, specVersion: String): LexerTokenProvider {
        val tokenRule = RuleGenerator.createTokenRule(specVersion)
        val reader = FileReader(File(filePath))
        val lexer = Lexer(reader, tokenRule)
        return LexerTokenProvider(lexer)
    }
}