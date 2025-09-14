/**
 * Interface for tokenization rules using Strategy pattern.
 * Each version of PrintScript will have its own implementation.
 */
interface TokenRule {
    val tokenRules: LinkedHashMap<String, TokenType>
}
