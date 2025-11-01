package progress

/**
 * Interface for reporting progress during CLI operations.
 */
interface ProgressReporter {
    /**
     * Report progress with an optional percentage.
     * @param message The progress message
     * @param percentage The completion percentage (0-100), or null if indeterminate
     */
    fun reportProgress(
        message: String,
        percentage: Int?,
    )

    /**
     * Report an error.
     * @param message The error message
     */
    fun reportError(message: String)

    /**
     * Report successful completion.
     * @param message The success message
     */
    fun reportSuccess(message: String)
}
