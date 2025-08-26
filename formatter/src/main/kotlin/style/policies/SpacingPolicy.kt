package style.policies

enum class BinaryOpSpacing { NONE, AROUND }

enum class AssignmentSpacing { NONE, AROUND }

enum class CommaSpacing { NONE, AFTER }

data class SpacingPolicy(
    val binaryOps: BinaryOpSpacing = BinaryOpSpacing.AROUND,
    val assignment: AssignmentSpacing = AssignmentSpacing.AROUND,
    val comma: CommaSpacing = CommaSpacing.AFTER,
)
