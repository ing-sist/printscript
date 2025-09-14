package validators

import AstNode
import ConditionalNode
import Result
import Token
import TokenProvider
import TokenType
import builders.ExpressionBuilder
import parser.ParseError
import validators.provider.ValidatorsProvider

class IfValidator(
    private val validatorsProvider: ValidatorsProvider,
) : AstValidator {
    override fun validateAndBuild(stream: TokenProvider): Result<AstNode, ParseError?> {
        // Valida si el token actual es 'if'
        if (stream.peek().type !is TokenType.Keyword.If) {
            return Result.Failure(null) // No es un if, pasa al siguiente validador
        }

        return try {
            parseIfStatement(stream)
        } catch (error: ParseError) {
            Result.Failure(error)
        }
    }

    private fun parseIfStatement(stream: TokenProvider): Result<AstNode, ParseError?> {
        stream.consume() // Consume 'if'

        var result: Result<AstNode, ParseError?>

        // Parseo de la condición
        if (stream.consume().type !is TokenType.LeftParen) {
            result = Result.Failure(ParseError.UnexpectedToken(stream.peek(-1), "'('"))
        } else {
            result = parseConditionAndBlocks(stream)
        }

        return result
    }

    private fun parseConditionAndBlocks(stream: TokenProvider): Result<AstNode, ParseError?> {
        val conditionTokens = mutableListOf<Token>()
        while (stream.peek().type !is TokenType.RightParen) {
            conditionTokens.add(stream.consume())
        }

        var result: Result<AstNode, ParseError?>

        if (conditionTokens.isEmpty()) {
            result =
                Result.Failure(
                    ParseError.InvalidSyntax(
                        emptyList(),
                        "La condición del if no puede estar vacía.",
                    ),
                )
        } else if (stream.consume().type !is TokenType.RightParen) {
            result = Result.Failure(ParseError.UnexpectedToken(stream.peek(-1), "')'"))
        } else {
            val conditionNode = ExpressionBuilder().build(conditionTokens)
            result = parseBlocks(stream, conditionNode)
        }

        return result
    }

    private fun parseBlocks(
        stream: TokenProvider,
        conditionNode: AstNode,
    ): Result<AstNode, ParseError?> {
        var result: Result<AstNode, ParseError?>

        // Parseo del bloque 'then'
        when (val thenResult = parseBlock(stream, validatorsProvider)) {
            is Result.Success -> {
                val thenBody = thenResult.value
                // Parseo opcional del bloque 'else'
                var elseBody: List<AstNode>? = null
                if (stream.peek().type is TokenType.Keyword.Else) {
                    stream.consume() // Consume 'else'
                    when (val elseResult = parseBlock(stream, validatorsProvider)) {
                        is Result.Success -> {
                            elseBody = elseResult.value
                            val node = ConditionalNode(conditionNode, thenBody, elseBody)
                            result = Result.Success(node)
                        }

                        is Result.Failure -> {
                            result =
                                Result.Failure(
                                    ParseError.InvalidSyntax(
                                        listOf(stream.peek()),
                                        "Se esperaba '{' para iniciar el bloque 'else'",
                                    ),
                                )
                        }
                    }
                } else {
                    val node = ConditionalNode(conditionNode, thenBody, elseBody)
                    result = Result.Success(node)
                }
            }

            is Result.Failure -> {
                result =
                    Result.Failure(
                        ParseError.InvalidSyntax(
                            listOf(stream.peek()),
                            "Se esperaba '{' para iniciar el bloque 'then'",
                        ),
                    )
            }
        }

        return result
    }

    /**
     * Función auxiliar que parsea un bloque de código entre llaves.
     */
    private fun parseBlock(
        stream: TokenProvider,
        validatorsProvider: ValidatorsProvider,
    ): Result<List<AstNode>, ParseError?> {
        if (stream.peek().type !is TokenType.LeftBrace) {
            return Result.Failure(ParseError.InvalidSyntax(listOf(stream.peek()), "Expected '{'"))
        }

        return parseBlockContent(stream, validatorsProvider)
    }

    private fun parseBlockContent(
        stream: TokenProvider,
        validatorsProvider: ValidatorsProvider,
    ): Result<List<AstNode>, ParseError?> {
        stream.consume() // Consume '{'

        val statements = mutableListOf<AstNode>()
        while (stream.peek().type !is TokenType.RightBrace &&
            stream.peek().type !is TokenType.EOF
        ) {
            when (val result = validatorsProvider.findValidatorAndBuild(stream)) {
                is Result.Success -> statements.add(result.value)
                is Result.Failure -> {
                    return Result.Failure(
                        result.error ?: ParseError.NoValidParser(listOf(stream.peek())),
                    )
                }
            }
        }

        return validateBlockClosure(stream, statements)
    }

    private fun validateBlockClosure(
        stream: TokenProvider,
        statements: List<AstNode>,
    ): Result<List<AstNode>, ParseError?> {
        // Validar si el bloque termina con la llave de cierre
        if (stream.peek().type !is TokenType.RightBrace) {
            return Result.Failure(
                ParseError.UnexpectedToken(stream.peek(), "'}'"),
            )
        }

        stream.consume() // Consume '}'
        return Result.Success(statements)
    }
}
