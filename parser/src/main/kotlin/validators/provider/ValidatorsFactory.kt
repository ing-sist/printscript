package validators.provider

import validators.AssignmentValidator
import validators.AstValidator
import validators.DeclarationAssignmentValidator
import validators.DeclarationValidator
import validators.FunctionCallValidator
import validators.IfValidator

object ValidatorsFactory {
    /**
     * Creates the appropriate validators for the specified PrintScript version.
     * Extensible design allows easy addition of new versions.
     *
     * @param version The PrintScript version (e.g., "1.0", "1.1")
     * @return List of validators for the specified version
     * @throws IllegalArgumentException if version is not supported
     */
    fun createValidators(version: String): List<AstValidator> =
        when (version) {
            "1.0" -> createV10Validators()
            "1.1" -> createV11Validators()
            // Add future versions here:
            // "1.2" -> createV12Validators()
            else -> throw IllegalArgumentException("Unsupported PrintScript version: $version")
        }

    /**
     * Creates validators for PrintScript 1.0.
     * Supports: declarations, assignments, function calls.
     */
    private fun createV10Validators(): List<AstValidator> =
        listOf(
            DeclarationAssignmentValidator(),
            DeclarationValidator(),
            AssignmentValidator(),
            FunctionCallValidator(),
        )

    /**
     * Creates validators for PrintScript 1.1.
     * Supports: all v1.0 features + if/else statements.
     *
     * IMPORTANT: IfValidator receives ALL validators (including itself) to support
     * nested if statements. This is safe because we're not creating a circular
     * dependency at construction time - the list is passed by reference.
     */
    private fun createV11Validators(): List<AstValidator> {
        val baseValidators =
            listOf(
                DeclarationAssignmentValidator(),
                DeclarationValidator(),
                AssignmentValidator(),
                FunctionCallValidator(),
            )

        // Create a mutable list that will contain all v1.1 validators
        val allValidators = mutableListOf<AstValidator>()

        // Create IfValidator with the full list (which includes itself for nested ifs)
        // This allows nested if statements to work correctly
        val ifValidator = IfValidator(allValidators)

        // Add all validators: IfValidator first (higher priority), then base validators
        allValidators.add(ifValidator)
        allValidators.addAll(baseValidators)

        return allValidators
    }
}
