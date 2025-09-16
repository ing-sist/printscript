package validators.helpers

import Token

object DeclarationHelper {
    fun matchesDeclarationPattern(tokens: List<Token>): Boolean {
        // Valida que el patrón sea let/const <id> : <type>
        return when {
            tokens.size < 4 -> false
            !validateKeyword(tokens[0]) -> false
            tokens[1].type !is TokenType.Identifier -> false
            tokens[2].type !is TokenType.Colon -> false
            !validateType(tokens[3]) -> false
            else -> true
        }
    }

    private fun validateType(token: Token): Boolean =
        token.type in listOf(TokenType.StringType, TokenType.NumberType, TokenType.BooleanType)

    private fun validateKeyword(token: Token): Boolean =
        token.type == TokenType.Keyword.VariableDeclaration || token.type == TokenType.Keyword.ConstDeclaration
}
