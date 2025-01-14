package de.fhkiel.rob.legoosctester.gui

import java.awt.*
import javax.swing.*

class MainGUI : JFrame() {

    private val mapCanvas = MapCanvas()

    init {
        val gridBagLayout = GridBagLayout()
        val constraints = GridBagConstraints()
        layout = gridBagLayout
        constraints.insets = Insets(10, 15, 10, 15)



        constraints.gridx = 0
        constraints.gridy = 1
        add(mapCanvas,constraints)



        constraints.gridx = 1
        constraints.gridy = 1
        add(JLabel("hier kommen roboter infos hin und steuereung"),constraints)

        constraints.gridwidth = 2
        constraints.gridx = 0
        constraints.gridy = 0
        constraints.anchor = GridBagConstraints.CENTER
        add(JLabel("Peter der tolle Roboter"),constraints)
        pack()

        isVisible = true
    }
}
