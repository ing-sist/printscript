/**
 * Represents a collection of tokenization rules for the lexer.
 * Uses a single LinkedHashMap for all token rules with regex patterns as keys.
 */
class TokenRule private constructor(
    val tokenRules: LinkedHashMap<String, TokenType>
) {

    class Builder {
        private val tokenRules = LinkedHashMap<String, TokenType>()

        fun addRule(regex: String, tokenType: TokenType): Builder {
            tokenRules[regex] = tokenType
            return this
        }

        fun addRules(rules: Map<String, TokenType>): Builder {
            tokenRules.putAll(rules)
            return this
        }

        fun build(): TokenRule {
            return TokenRule(LinkedHashMap(tokenRules))
        }
    }

    companion object {
        fun builder(): Builder = Builder()


        fun fromMap(rules: LinkedHashMap<String, TokenType>): TokenRule {
            return TokenRule(LinkedHashMap(rules))
        }
    }
}
