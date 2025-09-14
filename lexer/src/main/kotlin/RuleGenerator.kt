/**
 * Factory for creating TokenRule instances based on PrintScript version.
 * Uses Factory pattern with version-based selection strategy.
 */
object RuleGenerator {
    private const val VERSION_1_0 = "1.0"
    private const val VERSION_1_1 = "1.1"
    private const val DEFAULT_VERSION = VERSION_1_0

    /**
     * Creates token rules for the specified PrintScript version.
     * @param version The version string (e.g., "1.0", "1.1")
     * @return TokenRule implementation for the specified version
     * @throws IllegalArgumentException if version is not supported
     */
    fun createTokenRule(version: String): TokenRule =
        when (version) {
            VERSION_1_0 -> TokenRuleVersion10()
            VERSION_1_1 -> TokenRuleVersion11()
            else -> throw IllegalArgumentException(
                "Unsupported PrintScript version: $version. " +
                    "Supported versions: $VERSION_1_0, $VERSION_1_1",
            )
        }

    /**
     * Creates token rules for the default PrintScript version (1.0).
     */
    fun createDefaultTokenRule(): TokenRule = createTokenRule(DEFAULT_VERSION)

    /**
     * Gets all supported versions.
     */
    fun getSupportedVersions(): List<String> = listOf(VERSION_1_0, VERSION_1_1)

    /**
     * Checks if a version is supported.
     */
    fun isVersionSupported(version: String): Boolean = version in getSupportedVersions()
}
