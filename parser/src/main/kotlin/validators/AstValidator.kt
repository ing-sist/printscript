package validators

import AstNode
import Result
import TokenStream
import parser.ParseError

interface AstValidator {
    /**
     * Valida si la secuencia de tokens actual en el stream puede ser manejada por este validador.
     * Si es así, consume los tokens, construye y devuelve el AstNode.
     * @param stream El stream de tokens.
     * @return Un Result.Success con el AstNode si la validación y construcción son exitosas.
     * Un Result.Failure con null si la regla no coincide.
     * Un Result.Failure con un ParseError si la regla coincide pero hay un error de sintaxis.
     */
    fun validateAndBuild(stream: TokenStream): Result<AstNode, ParseError?>
}
