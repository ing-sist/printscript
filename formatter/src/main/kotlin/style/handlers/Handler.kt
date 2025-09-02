package style.handlers

import events.FormattingEvent
import style.policies.Policy

interface Handler<PolicyType : Policy> {
    fun handle(
        events: List<FormattingEvent>,
        policy: PolicyType,
    ): List<FormattingEvent>
}
