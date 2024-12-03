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

    fun start(port: Int = 9001){
        receiver = OSCPortIn(port)
        receiver.dispatcher.addListener(
            object : MessageSelector {
                override fun isInfoRequired(): Boolean {
                    return false
                }

                override fun matches(messageEvent: OSCMessageEvent?): Boolean {
                    return true
                }
            }
        ) { event ->
            if (event != null) {
                newMessage(event.message.address, event.message.arguments)
            }
        }
        thread { receiver.startListening() }

        this.port = port
    }

    fun stop(){
        if(this::receiver.isInitialized) {
            receiver.stopListening()
            this.port = -1
        }
    }

    private fun newMessage(path: String, args: List<Any>){
        log(path,"${args}")
    }
}