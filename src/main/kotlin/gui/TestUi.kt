package de.fhkiel.rob.legoosctester

import de.fhkiel.rob.legoosctester.gui.Incoming
import de.fhkiel.rob.legoosctester.gui.Outgoing
import de.fhkiel.rob.legoosctester.osc.OSCSender
import java.awt.Dimension
import java.awt.GridLayout
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JFrame.EXIT_ON_CLOSE
import javax.swing.JPanel

class TestGui: JFrame() {

    init {
        title = "ButtonKram"
        minimumSize =  Dimension(400, 400)
        defaultCloseOperation = EXIT_ON_CLOSE
        val robotController: RobotController = RobotController()

        layout = GridLayout(3, 3)
        add(JPanel())
        val forward = JButton("A")
        forward.addActionListener {
            OSCSender("192.168.178.152", 9001).send("/192.168.178.152/motor/bc/angle", 0)
            OSCSender("192.168.178.152", 9001).send("/192.168.178.152/motor/bc/run/target", 100, 360)
        }
        add(forward)

        add(JPanel())
        val left = JButton("<")
        left.addActionListener {
            OSCSender("192.168.178.152", 9001).send("/192.168.178.152/motor/bc/angle", 0)
            OSCSender("192.168.178.152", 9001).send("/192.168.178.152/motor/bc/multirun/target", 100, 180, -180)
        }
        add(left)
        add(JPanel())
        val right = JButton(">")
        right.addActionListener {
           robotController.turnRight90Degree()
        }
        add(right)
        add(JPanel())

        val back = JButton("V")
        back.addActionListener {
            OSCSender("192.168.178.152", 9001).send("/192.168.178.152/motor/bc/angle", 0)
            OSCSender("192.168.178.152", 9001).send("/192.168.178.152/motor/bc/run/target", -100, -360)
        }
        add(back)

        isVisible = true
    }

}