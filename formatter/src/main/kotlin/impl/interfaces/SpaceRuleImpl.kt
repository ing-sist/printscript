package impl.interfaces

import Token
import config.FormatterStyleConfig

interface SpaceRulesImpl : Rule

interface SpaceBeforeRule : SpaceRulesImpl {
    fun spaceBefore(
        curr: Token,
        style: FormatterStyleConfig,
    ): Boolean?
}

interface SpaceAfterRule : SpaceRulesImpl {
    fun spaceAfter(
        curr: Token,
        style: FormatterStyleConfig,
    ): Boolean?
}
