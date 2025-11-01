package progress

/**
 * Console-based progress reporter that displays a visual progress bar.
 * Uses carriage return (\r) to update the same line in the terminal.
 */
class ConsoleProgressReporter : ProgressReporter {
    private fun progressBar(percentage: Int): String {
        val barLength = 40 // Length of the progress bar
        val filledLength = (barLength * percentage) / 100
        val emptyLength = barLength - filledLength
        return "=".repeat(filledLength) + " ".repeat(emptyLength)
    }

    override fun reportProgress(
        message: String,
        percentage: Int?,
    ) {
        val percent = percentage?.coerceIn(0, 100) ?: -1

        if (percent >= 0) {
            val bar = progressBar(percent)
            // Print \r to return to the start of the line (overwrites previous output)
            print("\r[$bar] $percent% - $message")
        } else {
            // Indeterminate progress (no percentage)
            print("\r$message")
        }
        // Flush to ensure immediate display
        System.out.flush()
    }

    /**
     * Clears the progress line before a normal 'echo'.
     */
    fun clearProgressLine() {
        print("\r" + " ".repeat(80) + "\r") // Clear the line
        System.out.flush()
    }

    override fun reportError(message: String) {
        clearProgressLine()
        System.err.println("ERROR: $message")
        System.err.flush()
    }

    override fun reportSuccess(message: String) {
        clearProgressLine()
        println("✓ $message")
        System.out.flush()
    }
}
