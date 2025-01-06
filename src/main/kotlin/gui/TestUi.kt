package de.fhkiel.rob.legoosctester

import de.fhkiel.rob.legoosctester.gui.Incoming
import de.fhkiel.rob.legoosctester.gui.Outgoing
import de.fhkiel.rob.legoosctester.osc.OSCSender
import org.koin.mp.KoinPlatform.getKoin
import java.awt.Dimension
import java.awt.GridLayout
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JFrame.EXIT_ON_CLOSE
import javax.swing.JPanel

class TestGui: JFrame(

) {
    private val robotController: RobotController = getKoin().get()
    init {

        title = "ButtonKram"
        minimumSize =  Dimension(400, 400)
        defaultCloseOperation = EXIT_ON_CLOSE

        //val labyrinthExplorer: LabyrinthExplorer = getKoin().get()  // LabyrinthExplorer instanziieren


        layout = GridLayout(3, 3)
        add(JPanel())
        val forward = JButton("A")
        forward.addActionListener {
            robotController.turnEyes(EyesDirection.FRONT)
        }
        add(forward)

        add(JPanel())
        val left = JButton("<")
        left.addActionListener {
           robotController.turnEyes(EyesDirection.LEFT)
        }
        add(left)
        add(JPanel())
        val right = JButton(">")
        right.addActionListener {
            //labyrinthExplorer.explore()
        }
        add(right)
        add(JPanel())
        val back = JButton("V")
        back.addActionListener {
            robotController.turnEyes(EyesDirection.BACK)
        }
        add(back)

        isVisible = true
    }

}