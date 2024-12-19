package de.fhkiel.rob.legoosctester.osc

import com.illposed.osc.MessageSelector
import com.illposed.osc.OSCMessageEvent
import com.illposed.osc.transport.OSCPortIn
import de.fhkiel.rob.legoosctester.gui.Incoming.log
import kotlin.concurrent.thread

object OSCReceiver {
    var port: Int = 9001
        private set
    private lateinit var receiver: OSCPortIn

    private val listeners = mutableListOf<(String, List<Any>) -> Unit>()

    var prefixFilter: String = "/OSCBrick@192.168.178.152"

    fun addListener(listener: (String, List<Any>) -> Unit) {
        listeners.add(listener)
    }

    fun start(port: Int = 9001) {
        receiver = OSCPortIn(port)
        receiver.dispatcher.addListener(
            object : MessageSelector {
                override fun isInfoRequired(): Boolean = false

                override fun matches(messageEvent: OSCMessageEvent?): Boolean {
                    // Filter basierend auf dem Prefix
                    return messageEvent?.message?.address?.startsWith(prefixFilter) == true
                }
            }
        ) { event ->
            if (event != null) {
                val path = event.message.address
                val args = event.message.arguments
                listeners.forEach { it(path, args) }
                newMessage(path, args)
            }
        }
        thread { receiver.startListening() }

        this.port = port
    }

    fun stop() {
        if (this::receiver.isInitialized) {
            receiver.stopListening()
            this.port = -1
        }
    }

    private fun newMessage(path: String, args: List<Any>) {
        log(path, "${args}")
    }
}
