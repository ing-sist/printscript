sealed class ParseError {
    data class WrongTokenOrder(val token: Token) : ParseError()
}