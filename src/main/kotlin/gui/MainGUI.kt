package de.fhkiel.rob.legoosctester.gui

import org.koin.mp.KoinPlatform.getKoin
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
        constraints.fill = GridBagConstraints.BOTH
        add(mapCanvas, constraints)

        val controlPanel = ControlPanel()
        controlPanel.labyrinthExplorer = getKoin().get()
        controlPanel.automaticExplorer = getKoin().get()
        controlPanel.labyrinthState = getKoin().get()
        controlPanel.algorithmus = getKoin().get()

        constraints.gridx = 1
        constraints.gridy = 1
        constraints.anchor = GridBagConstraints.SOUTHEAST
        constraints.fill = GridBagConstraints.BOTH
        add(controlPanel, constraints)

        // Verbindung zwischen MapCanvas und ControlPanel herstellen
        mapCanvas.selectedCellListener = { position, cell ->
            controlPanel.updateCellInfo(position, cell)
        }

        constraints.gridwidth = 2
        constraints.gridx = 0
        constraints.gridy = 0
        constraints.anchor = GridBagConstraints.CENTER
        add(JLabel("Peter der tolle Roboter"), constraints)

        pack()
        isVisible = true
    }
}
