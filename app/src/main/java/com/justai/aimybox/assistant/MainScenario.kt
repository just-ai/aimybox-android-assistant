package com.justai.aimybox.assistant

import com.justai.jaicf.activator.caila.caila
import com.justai.jaicf.channel.aimybox.aimybox
import com.justai.jaicf.model.scenario.Scenario

object MainScenario: Scenario() {

    init {
        state("hello") {
            activators {
                intent("hello")
            }

            action {
                reactions.sayRandom("Hello!", "Hello there!")
            }
        }

        state("bye") {
            activators {
                intent("bye")
            }

            action {
                reactions.run {
                    say("Bye bye!")
                    aimybox?.endConversation()
                }
            }
        }

        state("smalltalk", noContext = true) {
            activators {
                anyIntent()
            }

            action {
                activator.caila?.topIntent?.answer?.let {
                    reactions.say(it)
                }
            }
        }

        fallback {
            reactions.sayRandom("Sorry, I didn't get that.")
        }
    }
}