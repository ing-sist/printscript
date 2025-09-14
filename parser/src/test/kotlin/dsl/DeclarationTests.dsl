# Declaration Tests - PrintScript 1.0 & 1.1

## Basic Variable Declarations

### Test: simple_let_declaration
INPUT:
let x: number;
EXPECTED_AST:
DeclarationNode(
    identifier = IdentifierNode(value = Token(Identifier, "x"), name = "x"),
    type = Token(NumberType, "number"),
    isMutable = true
)

### Test: simple_const_declaration
INPUT:
const y: string;
EXPECTED_AST:
DeclarationNode(
    identifier = IdentifierNode(value = Token(Identifier, "y"), name = "y"),
    type = Token(StringType, "string"),
    isMutable = false
)

### Test: boolean_let_declaration
INPUT:
let flag: boolean;
EXPECTED_AST:
DeclarationNode(
    identifier = IdentifierNode(value = Token(Identifier, "flag"), name = "flag"),
    type = Token(BooleanType, "boolean"),
    isMutable = true
)

### Test: boolean_const_declaration
INPUT:
const isValid: boolean;
EXPECTED_AST:
DeclarationNode(
    identifier = IdentifierNode(value = Token(Identifier, "isValid"), name = "isValid"),
    type = Token(BooleanType, "boolean"),
    isMutable = false
)

## Declaration with Different Types

### Test: string_declaration_with_spaces
INPUT:
let   name   :   string   ;
EXPECTED_AST:
DeclarationNode(
    identifier = IdentifierNode(value = Token(Identifier, "name"), name = "name"),
    type = Token(StringType, "string"),
    isMutable = true
)

### Test: number_declaration_underscore_identifier
INPUT:
let user_count: number;
EXPECTED_AST:
DeclarationNode(
    identifier = IdentifierNode(value = Token(Identifier, "user_count"), name = "user_count"),
    type = Token(NumberType, "number"),
    isMutable = true
)

### Test: camelCase_identifier
INPUT:
const userName: string;
EXPECTED_AST:
DeclarationNode(
    identifier = IdentifierNode(value = Token(Identifier, "userName"), name = "userName"),
    type = Token(StringType, "string"),
    isMutable = false
)

## Edge Cases

### Test: single_letter_identifier
INPUT:
let a: number;
EXPECTED_AST:
DeclarationNode(
    identifier = IdentifierNode(value = Token(Identifier, "a"), name = "a"),
    type = Token(NumberType, "number"),
    isMutable = true
)

### Test: identifier_with_numbers
INPUT:
let var123: string;
EXPECTED_AST:
DeclarationNode(
    identifier = IdentifierNode(value = Token(Identifier, "var123"), name = "var123"),
    type = Token(StringType, "string"),
    isMutable = true
)

## Error Cases

### Test: missing_semicolon
INPUT:
let x: number
EXPECTED_ERROR:
ParseError.UnexpectedToken(token = Token(EOF, ""), expected = "';'")

### Test: missing_colon
INPUT:
let x number;
EXPECTED_ERROR:
ParseError.NoValidParser

### Test: missing_type
INPUT:
let x: ;
EXPECTED_ERROR:
ParseError.NoValidParser

### Test: invalid_keyword
INPUT:
var x: number;
EXPECTED_ERROR:
ParseError.NoValidParser

### Test: missing_identifier
INPUT:
let : number;
EXPECTED_ERROR:
ParseError.NoValidParser
