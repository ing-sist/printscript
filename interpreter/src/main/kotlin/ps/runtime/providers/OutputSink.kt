package ps.runtime.providers

interface OutputSink {
    fun print(message: String)
}

class ConsoleOutputSink : OutputSink {
    override fun print(message: String) {
        println(message)
    }
}

class BufferedOutputSink : OutputSink {
    private val buffer = mutableListOf<String>()

    override fun print(message: String) {
        buffer.add(message)
    }

    fun getOutput(): List<String> = buffer.toList()

    fun clearOutput() {
        buffer.clear()
    }

    fun getJoinedOutput(): String = buffer.joinToString("\n")
}
