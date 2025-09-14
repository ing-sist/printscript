/**
 * Provee una abstracción sobre una fuente de tokens, permitiendo
 * el consumo y la pre-visualización (lookahead) sin acoplarse
 * a la implementación de un lexer.
 */
interface TokenProvider {
    /**
     * Espía el token en la posición k (0 es el actual, 1 es el siguiente, etc.).
     * No consume los tokens, solo los mira.
     */
    fun peek(k: Int = 0): Token

    /**
     * Consume y devuelve el token actual, avanzando el stream.
     */
    fun consume(): Token
}
