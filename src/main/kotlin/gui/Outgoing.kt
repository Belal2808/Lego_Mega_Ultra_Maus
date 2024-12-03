package de.fhkiel.rob.legoosctester.gui

import de.fhkiel.rob.legoosctester.osc.OSCSender
import java.awt.Dimension
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.time.LocalDateTime
import javax.swing.*


class Outgoing: JPanel() {
    init {
        layout = GridBagLayout()

        val log = JTextArea()
        log.isEditable = false

        val logConstraints = GridBagConstraints()
        logConstraints.gridx = 0;
        logConstraints.gridy = 0;
        logConstraints.weightx = 1.0
        logConstraints.weighty = 1.0
        logConstraints.fill = GridBagConstraints.BOTH
        val scroll = JScrollPane (log, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER)
        add(scroll, logConstraints)

        val control = JPanel()
        control.layout = GridBagLayout()

        val ipToSendTo = JTextField("255.255.255.255")
        setHint(ipToSendTo, "TargetIP")
        ipToSendTo.horizontalAlignment = JTextField.CENTER
        ipToSendTo.minimumSize =  Dimension(100, 27)
        ipToSendTo.preferredSize =  Dimension(100, 27)
        control.add(ipToSendTo)

        val portToSendTo = JTextField("9001")
        setHint(portToSendTo, "TargetPort")
        portToSendTo.horizontalAlignment = JTextField.CENTER
        portToSendTo.minimumSize =  Dimension(50, 27)
        portToSendTo.preferredSize =  Dimension(50, 27)
        control.add(portToSendTo)

        val message = JTextField()
        setHint(message, "osc message")
        message.minimumSize =  Dimension(50, 27)
        message.preferredSize =  Dimension(Int.MAX_VALUE, 27)
        val listOfOSC = mutableSetOf<String>()
        val sendMessage = {
            try {
                val sender = OSCSender(ipToSendTo.text, portToSendTo.text.toInt())
                val messageParts = message.text.split(" ")
                if(messageParts.size == 1){
                    sender.send(messageParts[0])
                } else {
                    sender.send(messageParts[0], parseArgs(messageParts.subList(1, messageParts.size)))
                }
                listOfOSC.add(message.text)
                log.append("${LocalDateTime.now().format(TIME_FORMATER)}: [${ipToSendTo.text}:${portToSendTo.text}] -> ${message.text}\n")
            } catch (e:Exception){
                log.append("${LocalDateTime.now().format(TIME_FORMATER)}: Error sending message: ${e.localizedMessage}\n")
            }
        }

        message.addActionListener{
            sendMessage()
        }


        val messageConstraints = GridBagConstraints()
        messageConstraints.weightx = 1.0
        messageConstraints.fill = GridBagConstraints.HORIZONTAL
        control.add(message, messageConstraints)


        val send = JButton("Send")
        send.addActionListener{
            sendMessage()
        }
        control.add(send)

        val controlConstraints = GridBagConstraints()
        controlConstraints.gridx = 0;
        controlConstraints.gridy = 1;
        controlConstraints.weightx = 1.0
        controlConstraints.fill = GridBagConstraints.HORIZONTAL
        add(control, controlConstraints)
    }
}