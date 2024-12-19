package de.fhkiel.rob.legoosctester.gui

import de.fhkiel.rob.legoosctester.TestGui
import java.awt.Dimension
import java.awt.GridLayout
import javax.swing.JFrame


class Base: JFrame() {

    init {
        title = "OSCTester"
        minimumSize =  Dimension(400, 400)
        size = Dimension(800, 800)
        defaultCloseOperation = EXIT_ON_CLOSE
        TestGui()
        layout = GridLayout(2,1)
        add(Outgoing())
        add(Incoming)

        isVisible = true
    }
}