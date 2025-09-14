// validators/IfValidator.kt
package validators

import AstNode
import ConditionalNode
import Result
import Token
import TokenProvider
import TokenType
import parser.ParseError
import builders.ExpressionBuilder // Asegúrate de tener este import
import validators.provider.ValidatorsProvider

class IfValidator(private val validatorsProvider: ValidatorsProvider) : AstValidator {

    override fun validateAndBuild(stream: TokenProvider): Result<AstNode, ParseError?> {
        // Valida si el token actual es 'if'
        if (stream.peek().type !is TokenType.IfKeyword) {
            return Result.Failure(null) // No es un if, pasa al siguiente validador
        }

        stream.consume() // Consume 'if'

        // Parseo de la condición
        if (stream.consume().type !is TokenType.LeftParen) {
            return Result.Failure(ParseError.UnexpectedToken(stream.peek(-1), "'('"))
        }

        val conditionTokens = mutableListOf<Token>()
        // Consume los tokens de la expresión de la condición hasta encontrar el ')'
        while (stream.peek().type !is TokenType.RightParen) {
            conditionTokens.add(stream.consume())
        }

        if (conditionTokens.isEmpty()) {
            return Result.Failure(ParseError.InvalidSyntax(emptyList(), "La condición del if no puede estar vacía."))
        }

        val conditionNode = ExpressionBuilder().build(conditionTokens)

        if (stream.consume().type !is TokenType.RightParen) {
            return Result.Failure(ParseError.UnexpectedToken(stream.peek(-1), "')'"))
        }

        // Parseo del bloque 'then'
        val thenBody = parseBlock(stream, validatorsProvider)
            ?: return Result.Failure(ParseError.InvalidSyntax(listOf(stream.peek()), "Se esperaba '{' para iniciar el bloque 'then'"))

        // Parseo opcional del bloque 'else'
        var elseBody: List<AstNode>? = null
        if (stream.peek().type is TokenType.ElseKeyword) {
            stream.consume() // Consume 'else'
            elseBody = parseBlock(stream, validatorsProvider)
                ?: return Result.Failure(ParseError.InvalidSyntax(listOf(stream.peek()), "Se esperaba '{' para iniciar el bloque 'else'"))
        }

        val node = ConditionalNode(conditionNode, thenBody, elseBody)
        return Result.Success(node)
    }

    /**
     * Función auxiliar que parsea un bloque de código entre llaves.
     * @param stream El stream de tokens.
     * @param validatorsProvider El proveedor de validadores para parsear las sentencias internas.
     * @return Una lista de AstNode si el bloque se parsea correctamente, o null si el primer token no es una llave de apertura.
     */
    private fun parseBlock(stream: TokenProvider, validatorsProvider: ValidatorsProvider): List<AstNode>? {
        if (stream.peek().type !is TokenType.LeftBrace) return null
        stream.consume() // Consume '{'

        val statements = mutableListOf<AstNode>()
        while (stream.peek().type !is TokenType.RightBrace && stream.peek().type !is TokenType.EOF) {
            when (val result = validatorsProvider.findValidatorAndBuild(stream)) {
                is Result.Success -> statements.add(result.value)
                is Result.Failure -> {
                    if (result.error != null) {
                        throw Exception("Error al parsear el bloque: ${result.error}")
                    }
                    // Si no hubo error, significa que no se encontró un validador válido, lo que es un problema en este contexto
                    throw ParseError.NoValidParser(listOf(stream.peek()))
                }
            }
        }

        // Validar si el bloque termina con la llave de cierre
        if (stream.peek().type !is TokenType.RightBrace) {
            throw ParseError.UnexpectedToken(stream.peek(), "'}'")
        }
        stream.consume() // Consume '}'

        return statements
    }
}