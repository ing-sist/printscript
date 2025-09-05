package progress

/**
 * Interface for reporting progress during long-running operations.
 * This provides user feedback while files are being processed.
 */
interface ProgressReporter {
    fun reportProgress(
        message: String,
        percentage: Int? = null,
    )

    fun reportError(message: String)

    fun reportSuccess(message: String)
}
