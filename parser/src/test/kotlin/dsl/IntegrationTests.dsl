# Integration & Edge Case Tests - Complete Parser Coverage

## Mixed Statement Types - PrintScript 1.0

### Test: complete_program_sequence_v10
INPUT:
let x: number;
x = 42;
let message: string = "Hello";
println(message + " World " + x);
EXPECTED_AST:
DeclarationNode(
    identifier = IdentifierNode(value = Token(Identifier, "x"), name = "x"),
    type = Token(NumberType, "number"),
    isMutable = true
)

### Test: arithmetic_with_function_call_v10
INPUT:
let result: number = 10 + 5;
println("Result is: " + result);
EXPECTED_AST:
DeclarationAssignmentNode(
    declaration = DeclarationNode(
        identifier = IdentifierNode(value = Token(Identifier, "result"), name = "result"),
        type = Token(NumberType, "number"),
        isMutable = true
    ),
    value = BinaryOperationNode(
        left = LiteralNode(value = Token(NumberLiteral, "10")),
        operator = Token(Plus, "+"),
        right = LiteralNode(value = Token(NumberLiteral, "5"))
    )
)

## Mixed Statement Types - PrintScript 1.1

### Test: complete_program_with_conditionals_v11
INPUT:
const isReady: boolean = true;
if (isReady) {
    let name: string = readInput("Enter name: ");
    println("Hello " + name);
    x = 100;
}
EXPECTED_AST:
DeclarationAssignmentNode(
    declaration = DeclarationNode(
        identifier = IdentifierNode(value = Token(Identifier, "isReady"), name = "isReady"),
        type = Token(BooleanType, "boolean"),
        isMutable = false
    ),
    value = LiteralNode(value = Token(BooleanLiteral, "true"))
)

### Test: nested_conditionals_with_all_statement_types
INPUT:
let debug: boolean;
debug = true;
if (debug) {
    const logLevel: string = readEnv("LOG_LEVEL");
    println("Debug mode: " + logLevel);
    if (logLevel) {
        let count: number = 0;
        count = count + 1;
        println("Count: " + count);
    } else {
        println("No logging");
    }
}
EXPECTED_AST:
DeclarationNode(
    identifier = IdentifierNode(value = Token(Identifier, "debug"), name = "debug"),
    type = Token(BooleanType, "boolean"),
    isMutable = true
)

## Streaming Parser Edge Cases

### Test: single_statement_streaming
INPUT:
println("Single statement");
EXPECTED_AST:
FunctionCallNode(
    functionName = "println",
    content = LiteralNode(value = Token(StringLiteral, "\"Single statement\"")),
    isVoid = true
)

### Test: minimal_declaration_streaming
INPUT:
let x: number;
EXPECTED_AST:
DeclarationNode(
    identifier = IdentifierNode(value = Token(Identifier, "x"), name = "x"),
    type = Token(NumberType, "number"),
    isMutable = true
)

### Test: minimal_if_streaming
INPUT:
if (true) {
}
EXPECTED_AST:
ConditionalNode(
    condition = LiteralNode(value = Token(BooleanLiteral, "true")),
    thenBody = [],
    elseBody = null
)

## Complex Identifier Patterns

### Test: underscore_identifiers
INPUT:
let user_name: string = "test_user";
const MAX_SIZE: number = 100;
_internal = _internal + 1;
EXPECTED_AST:
DeclarationAssignmentNode(
    declaration = DeclarationNode(
        identifier = IdentifierNode(value = Token(Identifier, "user_name"), name = "user_name"),
        type = Token(StringType, "string"),
        isMutable = true
    ),
    value = LiteralNode(value = Token(StringLiteral, "\"test_user\""))
)

### Test: mixed_case_identifiers
INPUT:
let camelCase: string;
let PascalCase: number;
let snake_case: boolean;
EXPECTED_AST:
DeclarationNode(
    identifier = IdentifierNode(value = Token(Identifier, "camelCase"), name = "camelCase"),
    type = Token(StringType, "string"),
    isMutable = true
)

## All Function Types in One Test

### Test: all_function_calls_v11
INPUT:
println("Starting");
let input: string = readInput("Enter value: ");
let env: string = readEnv("HOME");
println(input + " from " + env);
EXPECTED_AST:
FunctionCallNode(
    functionName = "println",
    content = LiteralNode(value = Token(StringLiteral, "\"Starting\"")),
    isVoid = true
)

## Boundary Value Tests

### Test: very_long_identifier
INPUT:
let thisIsAVeryLongIdentifierNameThatTestsTheBoundariesOfIdentifierLength: string = "test";
EXPECTED_AST:
DeclarationAssignmentNode(
    declaration = DeclarationNode(
        identifier = IdentifierNode(value = Token(Identifier, "thisIsAVeryLongIdentifierNameThatTestsTheBoundariesOfIdentifierLength"), name = "thisIsAVeryLongIdentifierNameThatTestsTheBoundariesOfIdentifierLength"),
        type = Token(StringType, "string"),
        isMutable = true
    ),
    value = LiteralNode(value = Token(StringLiteral, "\"test\""))
)

### Test: very_long_string_literal
INPUT:
println("This is a very long string literal that tests the ability to parse extremely long string values without issues in the streaming parser implementation");
EXPECTED_AST:
FunctionCallNode(
    functionName = "println",
    content = LiteralNode(value = Token(StringLiteral, "\"This is a very long string literal that tests the ability to parse extremely long string values without issues in the streaming parser implementation\"")),
    isVoid = true
)

### Test: large_number_literal
INPUT:
let bigNumber: number = 999999999.999999999;
EXPECTED_AST:
DeclarationAssignmentNode(
    declaration = DeclarationNode(
        identifier = IdentifierNode(value = Token(Identifier, "bigNumber"), name = "bigNumber"),
        type = Token(NumberType, "number"),
        isMutable = true
    ),
    value = LiteralNode(value = Token(NumberLiteral, "999999999.999999999"))
)

## Error Recovery and Validation

### Test: multiple_validation_failures
INPUT:
invalid syntax here;
EXPECTED_ERROR:
ParseError.NoValidParser

### Test: unterminated_string_in_stream
INPUT:
println("unterminated string
EXPECTED_ERROR:
LexerException

### Test: invalid_token_sequence
INPUT:
let 123invalid: string;
EXPECTED_ERROR:
ParseError.NoValidParser

### Test: unexpected_eof_in_expression
INPUT:
x = 1 +
EXPECTED_ERROR:
ParseError.UnexpectedToken(token = Token(EOF, ""), expected = "expression")

## Whitespace Stress Tests

### Test: excessive_whitespace_everywhere
INPUT:
   let    x    :    number    =    42    +    58    ;
EXPECTED_AST:
DeclarationAssignmentNode(
    declaration = DeclarationNode(
        identifier = IdentifierNode(value = Token(Identifier, "x"), name = "x"),
        type = Token(NumberType, "number"),
        isMutable = true
    ),
    value = BinaryOperationNode(
        left = LiteralNode(value = Token(NumberLiteral, "42")),
        operator = Token(Plus, "+"),
        right = LiteralNode(value = Token(NumberLiteral, "58"))
    )
)

### Test: minimal_whitespace_everywhere
INPUT:
let x:number=42+58;
EXPECTED_AST:
DeclarationAssignmentNode(
    declaration = DeclarationNode(
        identifier = IdentifierNode(value = Token(Identifier, "x"), name = "x"),
        type = Token(NumberType, "number"),
        isMutable = true
    ),
    value = BinaryOperationNode(
        left = LiteralNode(value = Token(NumberLiteral, "42")),
        operator = Token(Plus, "+"),
        right = LiteralNode(value = Token(NumberLiteral, "58"))
    )
)

## Special Characters in Strings

### Test: string_with_escaped_quotes
INPUT:
println("She said \"Hello\" to me");
EXPECTED_AST:
FunctionCallNode(
    functionName = "println",
    content = LiteralNode(value = Token(StringLiteral, "\"She said \\\"Hello\\\" to me\"")),
    isVoid = true
)

### Test: string_with_mixed_quotes
INPUT:
println("It's a 'test' string");
EXPECTED_AST:
FunctionCallNode(
    functionName = "println",
    content = LiteralNode(value = Token(StringLiteral, "\"It's a 'test' string\"")),
    isVoid = true
)

## Version-Specific Error Cases

### Test: const_in_v10_should_fail
INPUT:
const x: number = 42;
VERSION: 1.0
EXPECTED_ERROR:
ParseError.NoValidParser

### Test: if_statement_in_v10_should_fail
INPUT:
if (true) { println("test"); }
VERSION: 1.0
EXPECTED_ERROR:
ParseError.NoValidParser

### Test: boolean_type_in_v10_should_fail
INPUT:
let flag: boolean;
VERSION: 1.0
EXPECTED_ERROR:
ParseError.NoValidParser

### Test: readInput_in_v10_should_fail
INPUT:
readInput("prompt");
VERSION: 1.0
EXPECTED_ERROR:
ParseError.NoValidParser

## Zero and Negative Numbers

### Test: zero_assignments
INPUT:
let zero: number = 0;
let negZero: number = -0;
let decimal: number = 0.0;
EXPECTED_AST:
DeclarationAssignmentNode(
    declaration = DeclarationNode(
        identifier = IdentifierNode(value = Token(Identifier, "zero"), name = "zero"),
        type = Token(NumberType, "number"),
        isMutable = true
    ),
    value = LiteralNode(value = Token(NumberLiteral, "0"))
)
