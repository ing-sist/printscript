package style.policies

data class SpacingPolicy(
    val binaryOp: BinaryOpSpacing = BinaryOpSpacing.AROUND,
    val assignment: AssignmentSpacing = AssignmentSpacing.AROUND,
    val comma: CommaSpacing = CommaSpacing.AFTER,
    val paren: ParenSpacing = ParenSpacing.NONE,
    val bracket: BracketSpacing = BracketSpacing.NONE,
    val keyword: KeywordSpacing = KeywordSpacing.AFTER,
    val arrow: ArrowSpacing = ArrowSpacing.AROUND, // para los lambdas ->
    val colon: ColonSpacing = ColonSpacing.AFTER,
    val semicolon: SemicolonSpacing = SemicolonSpacing.NONE,
) : Policy

enum class BinaryOpSpacing { NONE, AROUND }

enum class AssignmentSpacing { NONE, AROUND }

enum class CommaSpacing { NONE, AFTER }

enum class ParenSpacing { NONE, INSIDE }

enum class BracketSpacing { NONE, INSIDE }

enum class KeywordSpacing { NONE, AFTER }

enum class ArrowSpacing { NONE, AROUND }

enum class ColonSpacing { NONE, AFTER }

enum class SemicolonSpacing { NONE, AFTER }
