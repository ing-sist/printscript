# Declaration Assignment Tests - PrintScript 1.0 & 1.1

## Basic Declaration Assignments

### Test: let_number_declaration_assignment
INPUT:
let x: number = 42;
EXPECTED_AST:
DeclarationAssignmentNode(
    declaration = DeclarationNode(
        identifier = IdentifierNode(value = Token(Identifier, "x"), name = "x"),
        type = Token(NumberType, "number"),
        isMutable = true
    ),
    value = LiteralNode(value = Token(NumberLiteral, "42"))
)

### Test: const_string_declaration_assignment
INPUT:
const name: string = "John";
EXPECTED_AST:
DeclarationAssignmentNode(
    declaration = DeclarationNode(
        identifier = IdentifierNode(value = Token(Identifier, "name"), name = "name"),
        type = Token(StringType, "string"),
        isMutable = false
    ),
    value = LiteralNode(value = Token(StringLiteral, "\"John\""))
)

### Test: let_boolean_declaration_assignment_true
INPUT:
let flag: boolean = true;
EXPECTED_AST:
DeclarationAssignmentNode(
    declaration = DeclarationNode(
        identifier = IdentifierNode(value = Token(Identifier, "flag"), name = "flag"),
        type = Token(BooleanType, "boolean"),
        isMutable = true
    ),
    value = LiteralNode(value = Token(BooleanLiteral, "true"))
)

### Test: const_boolean_declaration_assignment_false
INPUT:
const isValid: boolean = false;
EXPECTED_AST:
DeclarationAssignmentNode(
    declaration = DeclarationNode(
        identifier = IdentifierNode(value = Token(Identifier, "isValid"), name = "isValid"),
        type = Token(BooleanType, "boolean"),
        isMutable = false
    ),
    value = LiteralNode(value = Token(BooleanLiteral, "false"))
)

## Expression Assignments in Declarations

### Test: let_arithmetic_expression_declaration
INPUT:
let result: number = 10 + 5;
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

### Test: const_string_concatenation_declaration
INPUT:
const fullName: string = firstName + " " + lastName;
EXPECTED_AST:
DeclarationAssignmentNode(
    declaration = DeclarationNode(
        identifier = IdentifierNode(value = Token(Identifier, "fullName"), name = "fullName"),
        type = Token(StringType, "string"),
        isMutable = false
    ),
    value = BinaryOperationNode(
        left = BinaryOperationNode(
            left = IdentifierNode(value = Token(Identifier, "firstName"), name = "firstName"),
            operator = Token(Plus, "+"),
            right = LiteralNode(value = Token(StringLiteral, "\" \""))
        ),
        operator = Token(Plus, "+"),
        right = IdentifierNode(value = Token(Identifier, "lastName"), name = "lastName")
    )
)

### Test: let_complex_arithmetic_declaration
INPUT:
let area: number = width * height / 2;
EXPECTED_AST:
DeclarationAssignmentNode(
    declaration = DeclarationNode(
        identifier = IdentifierNode(value = Token(Identifier, "area"), name = "area"),
        type = Token(NumberType, "number"),
        isMutable = true
    ),
    value = BinaryOperationNode(
        left = BinaryOperationNode(
            left = IdentifierNode(value = Token(Identifier, "width"), name = "width"),
            operator = Token(Multiply, "*"),
            right = IdentifierNode(value = Token(Identifier, "height"), name = "height")
        ),
        operator = Token(Divide, "/"),
        right = LiteralNode(value = Token(NumberLiteral, "2"))
    )
)

### Test: const_parenthesized_expression_declaration
INPUT:
const result: number = (a + b) * c;
EXPECTED_AST:
DeclarationAssignmentNode(
    declaration = DeclarationNode(
        identifier = IdentifierNode(value = Token(Identifier, "result"), name = "result"),
        type = Token(NumberType, "number"),
        isMutable = false
    ),
    value = BinaryOperationNode(
        left = BinaryOperationNode(
            left = IdentifierNode(value = Token(Identifier, "a"), name = "a"),
            operator = Token(Plus, "+"),
            right = IdentifierNode(value = Token(Identifier, "b"), name = "b")
        ),
        operator = Token(Multiply, "*"),
        right = IdentifierNode(value = Token(Identifier, "c"), name = "c")
    )
)

## Decimal Numbers

### Test: let_decimal_declaration_assignment
INPUT:
let pi: number = 3.14159;
EXPECTED_AST:
DeclarationAssignmentNode(
    declaration = DeclarationNode(
        identifier = IdentifierNode(value = Token(Identifier, "pi"), name = "pi"),
        type = Token(NumberType, "number"),
        isMutable = true
    ),
    value = LiteralNode(value = Token(NumberLiteral, "3.14159"))
)

### Test: const_negative_decimal_declaration
INPUT:
const temperature: number = -25.5;
EXPECTED_AST:
DeclarationAssignmentNode(
    declaration = DeclarationNode(
        identifier = IdentifierNode(value = Token(Identifier, "temperature"), name = "temperature"),
        type = Token(NumberType, "number"),
        isMutable = false
    ),
    value = UnaryOperationNode(
        operator = Token(Minus, "-"),
        operand = LiteralNode(value = Token(NumberLiteral, "25.5"))
    )
)

## String Literals with Different Quotes

### Test: let_single_quote_string_declaration
INPUT:
let message: string = 'Hello World';
EXPECTED_AST:
DeclarationAssignmentNode(
    declaration = DeclarationNode(
        identifier = IdentifierNode(value = Token(Identifier, "message"), name = "message"),
        type = Token(StringType, "string"),
        isMutable = true
    ),
    value = LiteralNode(value = Token(StringLiteral, "'Hello World'"))
)

### Test: const_double_quote_string_declaration
INPUT:
const greeting: string = "Hello World";
EXPECTED_AST:
DeclarationAssignmentNode(
    declaration = DeclarationNode(
        identifier = IdentifierNode(value = Token(Identifier, "greeting"), name = "greeting"),
        type = Token(StringType, "string"),
        isMutable = false
    ),
    value = LiteralNode(value = Token(StringLiteral, "\"Hello World\""))
)

## With Spaces

### Test: declaration_assignment_with_spaces
INPUT:
let   x   :   number   =   42   ;
EXPECTED_AST:
DeclarationAssignmentNode(
    declaration = DeclarationNode(
        identifier = IdentifierNode(value = Token(Identifier, "x"), name = "x"),
        type = Token(NumberType, "number"),
        isMutable = true
    ),
    value = LiteralNode(value = Token(NumberLiteral, "42"))
)

## Error Cases

### Test: missing_assignment_value
INPUT:
let x: number = ;
EXPECTED_ERROR:
ParseError.UnexpectedToken(token = Token(Semicolon, ";"), expected = "expression")

### Test: missing_semicolon_declaration_assignment
INPUT:
let x: number = 42
EXPECTED_ERROR:
ParseError.UnexpectedToken(token = Token(EOF, ""), expected = "';'")

### Test: missing_assignment_operator_in_declaration
INPUT:
let x: number 42;
EXPECTED_ERROR:
ParseError.NoValidParser

### Test: invalid_type_in_declaration_assignment
INPUT:
let x: invalid = 42;
EXPECTED_ERROR:
ParseError.NoValidParser
