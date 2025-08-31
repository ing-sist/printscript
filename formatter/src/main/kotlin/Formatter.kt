import events.DocBuilder
import events.FormattingEvent
import events.mapToken
import style.StyleConfig
import style.handlers.ArgsLayoutHandler
import style.handlers.CommentHandler
import style.handlers.LineBreakHandler
import style.handlers.LineWrapHandler
import style.handlers.SpacingHandler
import style.handlers.WhiteSpaceTrimHandler

class Formatter(
    private val style: StyleConfig,
) {
    fun format(input: List<Token>): String {
        val out = DocBuilder()
        val events = mutableListOf<FormattingEvent>()

        // paso el input de token type a formatting event
        for (token in input) {
            val ev = mapToken(token)
            events.add(ev)
        }

        // paso la mut list a una var para reasignarla durante el pipeline
        var evs: List<FormattingEvent> = events

        evs = WhiteSpaceTrimHandler.handle(evs, style.whitespaceTrim)
        evs = CommentHandler.handle(evs, style.comments)
        evs = BlankLineHandler.handle(evs, style.blankLines)

        evs = ArgsLayoutHandler.handle(evs, style.argsLayout)
        evs = IndentationHandler.handle(evs, style.indentation)

        evs = SpacingHandler.handle(evs, style.spacing)

        evs = LineBreakHandler.handle(evs, style.lineBreak)
        evs = LineWrapHandler.handle(evs, style.lineWrap)

        // como cada handler sabe si tiene que cambiar esa list
        for (e in evs) {
            e.printer(out)
            // ahora la imrpimo en el doc builder
        }
        return out.build() // la paso a str
    }
}
