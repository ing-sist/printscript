package parser

import AstNode
import Result
import Token
import validators.ValidatorsProvider

class Parser(
    private val validatorsProvider: ValidatorsProvider,
) {
    fun parse(tokens: List<Token>): Result<List<AstNode>, ParseError> {
        val statements = mutableListOf<AstNode>()
        val tokenGroups = groupTokensByStatements(tokens)

        for (tokenGroup in tokenGroups) {
            when (val validatorResult = validatorsProvider.getValidator(tokenGroup)) {
                is Result.Success -> {
                    val astNode = validatorResult.value.build(tokenGroup)
                    statements.add(astNode)
                }
                is Result.Failure -> {
                    return Result.Failure(validatorResult.error)
                }
            }
        }
        return Result.Success(statements)
    }

    private fun groupTokensByStatements(tokens: List<Token>): List<List<Token>> {
        val statements = mutableListOf<List<Token>>()
        val currentStatement = mutableListOf<Token>()

        for (token in tokens) {
            if (isEndOfFile(token)) {
                break
            }

            currentStatement.add(token)

            if (isStatementTerminator(token)) {
                statements.add(currentStatement.toList())
                currentStatement.clear()
            }
        }

        return statements
    }

    private fun isEndOfFile(token: Token): Boolean = token.type is TokenType.EOF

    private fun isStatementTerminator(token: Token): Boolean = token.type is TokenType.Semicolon
}
