package viejos.progress

/**
 * Console implementation of ProgressReporter that outputs to stdout/stderr.
 */
class ConsoleProgressReporter : ProgressReporter {
    override fun reportProgress(
        message: String,
        percentage: Int?,
    ) {
        val progressText = if (percentage != null) "[$percentage%] " else ""
        println("${progressText}$message")
    }

    override fun reportError(message: String) {
        System.err.println("ERROR: $message")
    }

    override fun reportSuccess(message: String) {
        println("SUCCESS: $message")
    }
}
