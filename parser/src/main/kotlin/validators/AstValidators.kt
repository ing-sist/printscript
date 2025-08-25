package validators

import Result
import Token
import builders.AstBuilder
import parser.ParseError

interface AstValidators {
    fun validate(tokens: List<Token>): Result<AstBuilder, ParseError>
}
