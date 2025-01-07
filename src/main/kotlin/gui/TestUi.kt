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
        val labyrinthExplorer: LabyrinthExplorer = getKoin().get()
        title = "ButtonKram"
        minimumSize =  Dimension(400, 400)
        defaultCloseOperation = EXIT_ON_CLOSE

        //val labyrinthExplorer: LabyrinthExplorer = getKoin().get()  // LabyrinthExplorer instanziieren


        layout = GridLayout(3, 3)
        add(JPanel())
        val forward = JButton("scan")
        forward.addActionListener {
            labyrinthExplorer.scanCell()
        }
        add(forward)

        add(JPanel())
        val left = JButton("print")
        left.addActionListener {
           labyrinthExplorer.printCellBoarders()
        }
        add(left)
        add(JPanel())
        val right = JButton(">")
        right.addActionListener {
            //labyrinthExplorer.explore()
        }
        add(right)
        add(JPanel())
        val back = JButton("move South")
        back.addActionListener {
            labyrinthExplorer.driveSouth()
        }
        add(back)

        isVisible = true
    }

}