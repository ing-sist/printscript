package ps.runtime.providers

interface InputProvider {
    fun readInput(prompt: String): String
}

class StdinInputProvider : InputProvider {
    override fun readInput(prompt: String): String {
        print(prompt)
        return readLine() ?: ""
    }
}

class ProgrammaticInputProvider(
    private val inputs: MutableList<String>,
) : InputProvider {
    override fun readInput(prompt: String): String {
        if (inputs.isEmpty()) {
            error("No more inputs available")
        }
        return inputs.removeAt(0)
    }
}
