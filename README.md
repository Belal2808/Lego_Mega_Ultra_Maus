# SLAMing im Labyrinth

## Projektübersicht
Dieses Projekt ist ein Gradle-basiertes Softwareprojekt, das die autonome Kartografierung und Navigation eines Labyrinths durch einen Roboter ermöglicht. Das Hauptziel besteht darin, mithilfe von Sensoren und Algorithmen wie SLAM (Simultane Lokalisierung und Kartenerstellung) eine digitale Karte des Labyrinths zu erstellen und farbige Wegpunkte zu verwenden, um den Roboter präzise zu steuern.

## Kernfunktionalitäten
- **Kartografierung des Labyrinths**: Autonome Erstellung einer digitalen Karte.
- **Autonome Navigation**: Nutzung der Karte zur Bewegung durch das Labyrinth.
- **Interaktive Steuerung**: GUI-Elemente zur manuellen Steuerung und Verwaltung.

## Anforderungen
- **Java**: Version 11 oder höher
- **Gradle**: Version 7.x
- **Abhängigkeiten**: Siehe `build.gradle` für eine vollständige Liste der genutzten Bibliotheken.

## Nutzung
1. **Projekt einrichten**:
    - Lade alle Abhängigkeiten: `gradle build`

2. **Starten der Anwendung**:
    - Führe die Anwendung aus: `gradle run`
    - Führe `Main.kt` aus, um das Projekt zu starten.

3. **Interaktion über die GUI**:
   Die Steuerung erfolgt über die grafische Benutzeroberfläche (GUI), die folgende Buttons enthält:

### Beschreibung der Buttons

#### Zellinformationen
- **Speichern**: Speichert die aktuellen Informationen der ausgewählten Zelle (Position, Farbe, Wände etc.) in den Labyrinthzustand.

#### Bewegungssteuerung
- **↑ Nach Norden**: Bewegt den Roboter eine Zelle nach Norden.
- **↓ Nach Süden**: Bewegt den Roboter eine Zelle nach Süden.
- **← Nach Westen**: Bewegt den Roboter eine Zelle nach Westen.
- **→ Nach Osten**: Bewegt den Roboter eine Zelle nach Osten.

#### Labyrinthaktionen
- **🔍 Zelle scannen**: Führt eine Sensorauswertung der aktuellen Zelle durch.
- **🚶 Automatisch erkunden**: Startet den automatischen Erkundungsmodus des Roboters.
- **💾 Karte exportieren**: Exportiert die aktuelle Labyrinthkarte in die Datei `labyrinth.json`. Bei jedem Export wird die Karte mit einem Index (_1, _2, usw.) versioniert. Beachte, dass beim Importieren nur die Datei `labyrinth.json` geladen wird. Um eine spezifische Version zu verwenden, kopiere die gewünschte Datei manuell oder benenne sie in `labyrinth.json` um.
- **📂 Karte laden**: Lädt eine gespeicherte Karte aus der Datei `labyrinth.json`.
- **🎯 Nächstes Ziel finden**: Berechnet den nächsten Wegpunkt basierend auf dem aktuellen Zustand und bewegt den Roboter dorthin.
- **🔄 Zurücksetzen**: Setzt den Zustand des Roboters und des Labyrinths zurück.

## Codeüberblick
Das Projekt enthält die folgenden Schlüsselklassen:
- **`ControlPanel`**: Definiert die GUI-Steuerungselemente.
- **`LabyrinthExplorer`**: Handhabt die Bewegungslogik des Roboters.
- **`AutomaticExplorer`**: Implementiert den Algorithmus zur automatischen Erkundung.
- **`MapLoader`**: Lädt und speichert Labyrinthkarten.
- **`Algorithmus`**: Berechnet die optimale Navigation.

## Programm für den LEGO-Roboter
Das Programm, das auf dem LEGO-Roboter ausgeführt werden soll, befindet sich im Repository [legoOSC](https://github.com/WerthersEchte/legoOSC). Es ermöglicht die Kommunikation über OSC und die Steuerung des Roboters. Weitere Details zur Installation und Nutzung findest du im Repository.

## Erweiterungen
- **Integration neuer Sensoren**: Zusätzliche Sensoren können leicht über die `LabyrinthExplorer`-Klasse integriert werden.
- **Anpassung des Algorithmus**: Der Algorithmus kann in der `Algorithmus`-Klasse optimiert oder ersetzt werden.

## Fehlersuche
- Falls die Karte nicht korrekt exportiert oder geladen wird, überprüfe, ob die Datei `labyrinth.json` schreibgeschützt ist oder an einem anderen Ort liegt.
- Stelle sicher, dass der Roboter korrekt initialisiert wurde, bevor du automatische Aktionen startest.

## Autor
Belal Kattan & Tjark Seemann

## Lizenz
Dieses Projekt steht unter der MIT-Lizenz. Details siehe `LICENSE`. 

