# Inhaltsverzeichnis

1. [Einleitung](#einleitung)
2. [Analyse des allgemeinen Problems](#analyse)
3. [Glossar](#glossar)
4. [Lösungsansätze](#loesungsansaetze)
5. [Softwarearchitektur](#architektur)
   - [Use-Case Diagramm](#use-case)
   - [Klassendiagramm](#klassen)
6. [Vergleich der Algorithmen](#vergleich)
7. [Reflexion](#reflexion)
8. [Quellenverzeichnis](#quellen)

# 1. Einleitung <a name="einleitung"></a>

Das **Traveling Salesperson Problem** (TSP) stellt eines der bekanntesten und meistuntersuchten Probleme der kombinatorischen Optimierung dar. Die Aufgabe, eine kürzeste Rundreise durch eine Menge von Städten zu finden, erscheint zunächst einfach, entpuppt sich jedoch als hochkomplexes mathematisches Problem. Seine Bedeutung für die theoretische Informatik liegt in seiner NP-Vollständigkeit, während seine praktische Relevanz von der Logistikoptimierung bis zur Genomsequenzierung reicht.

Im Rahmen dieses Protokolls werden drei unterschiedliche Lösungsansätze in Java implementiert und verglichen: Als eine eigen abgeänderte Herangehensweise, der **Nearest-Neighbor**-Algorithmus, der stets die günstigste Route bis zur nächsten noch nicht besuchten Stadt berechnet, das **Branch-and-Bound**-Verfahren als exakter Algorithmus, der die optimale Lösung garantiert, sowie die **Ant Colony Optimization** (ACO) als naturinspirierte Heuristik für effiziente Näherungslösungen. Die Implementierung erfolgt in Java mit besonderem Fokus auf objektorientiertes Design und wird durch eine grafische Benutzeroberfläche ergänzt (GUI). Die praktische Anwendung der Algorithmen erfolgt im Kontext der Optimierung von Verteilungsrouten für die vorgegebenen Städte, wobei sowohl Lösungsqualität als auch Laufzeitverhalten verglichen werden.

# 2. Analyse des allgemeinen Problems <a name="analyse"></a>

#### Mathematische Formulierung

Das TSP wird als Graph **"G = (V,E)"** dargestellt:

- Die zu besuchenden Knoten: $$V$$
- Kanten zwischen den Orten (Verbindungen): $$E$$
- Kosten für jede Kante von Ort zu Ort: $$c_{ij}$$
- Kanten nutzung (1 = in benutzt; 0 = nicht in benutzung): $$x_{ij}$$

##### Zielfunktion:

$$\min \sum_{i=1}^n \sum_{j=1}^n c_{ij}x_{ij}$$

Mit den Bedingungen:

- Jeder Ort wird genau einmal besucht: $$\sum_{j=1}^n x_{ij} = 1$$
- Von jedem Ort wird genau einmal abgereist: $$\sum_{i=1}^n x_{ij} = 1$$

### Komplexitätsanalyse

- Das TSP ist NP-vollständig
- Bei **"n"** Orten gibt es $$(n-1)!/2$$ mögliche Routen

- Beispiele für die Anzahl der Routen:

| Anzahl Orte | Mögliche Routen |
| ----------- | --------------- |
| 5           | 12              |
| 10          | 181.440         |
| 15          | 43.589.145.600  |


### Anwendungsbereiche

1. Logistik

   - Paketauslieferung
   - Tourenplanung
   - Warentransport

2. Produktion

   - Maschinensteuerung
   - Leiterplattenherstellung
   - Lagerlogistik

3. Informatik
   - Netzwerkoptimierung
   - Serverplatzierung
   - Datenverteilung

# 3. Glossar <a name="glossar"></a>

| Begriff             | Definition                                                                                                                                                                                  |
| ------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| TSP                 | Traveling Salesperson Problem - Optimierungsproblem zur Findung der kürzesten Rundreise durch mehrere Orte                                                                                  |
| ACO                 | Ant Colony Optimization - Algorithmus, der das Verhalten von Ameisen bei der Futtersuche nachahmt                                                                                           |
| Branch-and-Bound    | Exaktes Lösungsverfahren, das den Lösungsraum systematisch durchsucht und nicht-optimale Lösungen ausschließt                                                                               |
| GUI                 | Graphical User Interface - Grafische Benutzeroberfläche zur Bedienung des Programms                                                                                                         |
| JavaFX              | Java-Framework zur Entwicklung von grafischen Benutzeroberflächen                                                                                                                           |
| Heuristik           | Näherungsverfahren, das schnell gute, aber nicht unbedingt optimale Lösungen findet                                                                                                         |
| Nearest Neighbor    | Einfacher Algorithmus, der immer zum nächstgelegenen noch nicht besuchten Ort geht                                                                                                          |
| Optimale Lösung     | Die bestmögliche Lösung für ein Problem (beim TSP: kürzeste mögliche Route)                                                                                                                 |
| NP-vollständig      | Bezeichnet Probleme, für die keine effizienten Algorithmen bekannt sind und die Rechenzeit mit der Größe extrem ansteigt                                                                    |
| Laufzeitkomplexität | Beschreibt, wie stark die Rechenzeit mit der Problemgröße wächst                                                                                                                            |
| Pruning             | "Beschneiden" - Technik beim Branch-and-Bound, um nicht-optimale Lösungszweige auszuschließen                                                                                               |
| Pheromon            | Beim ACO: Virtueller "Duftstoff", der gute Routen markiert                                                                                                                                  |
| Iterativ            | Schrittweise Vorgehensweise mit Wiederholungen                                                                                                                                              |
| Determinismus       | Eigenschaft eines Algorithmus, bei gleichen Eingaben immer gleiche Ausgaben zu liefern                                                                                                      |
| Parallelisierung    | Aufteilung der Berechnung auf mehrere Prozessoren zur Beschleunigung                                                                                                                        |
| Threading           | Parallele Ausführung von Programmteilen, wichtig für responsive GUIs                                                                                                                        |
| Parameter           | Einstellbare Werte, die das Verhalten eines Algorithmus beeinflussen                                                                                                                        |
| Bound               | Schranke - Abschätzung der minimal möglichen Weglänge beim Branch-and-Bound                                                                                                                 |
| Optimierungsproblem | Mathematisches Problem, bei dem die beste Lösung unter bestimmten Bedingungen gesucht wird                                                                                                  |
| Konvergenz          | Beschreibt, wie schnell sich ein Algorithmus der optimalen Lösung annähert                                                                                                                  |
| Fuzzy-search        | Eine Fuzzy Search ist eine Suchmethode, die auch bei ungenauen Eingaben oder Tippfehlern passende Ergebnisse findet, indem sie die Ähnlichkeit zwischen Suchbegriff und Zieltext berechnet. |

# 4. Lösungsansätze <a name="loesungsansaetze"></a>

Die Lösungsansätze für das TSP lassen sich in drei Hauptkategorien einteilen:

1. Exakte Verfahren (wie Branch and Bound)
2. Heuristische Verfahren (wie Ant Colony Optimization)
3. Modifizierte klassische Verfahren (wie unser angepasster Nearest Neighbor)

## Branch and Bound-Verfahren

Branch and Bound ist ein exaktes Verfahren, das die optimale Lösung garantiert findet. Es arbeitet nach dem Prinzip der systematischen Suche und des Ausschlusses suboptimaler Lösungen.

### Funktionsweise:

1. **Branching (Verzweigung)**

   - Aufteilung in Teilprobleme
   - Aufbau eines Suchbaums
   - Systematische Exploration möglicher Routen

2. **Bounding (Schrankenberechnung)**

   - Berechnung unterer Schranken für Teilprobleme
   - Abschätzung der minimalen Tourlänge
   - Nutzung von Minimalen Spannbäumen

3. **Pruning (Beschneiden)**
   - Ausschluss von Teilproblemen
   - Reduzierung des Suchraums
   - Fokussierung auf vielversprechende Bereiche

### Algorithmus:

```Pseudo
function branchAndBound(cities):
    bestTour = null
    bestCost = infinity
    queue = new PriorityQueue()
    queue.add(new Node(startCity))

    while !queue.isEmpty():
        node = queue.removeMin()
        if node.bound < bestCost:
            if isCompleteTour(node):
                updateBestTour(node)
            else:
                for city in unvisitedCities(node):
                    newNode = createNode(node, city)
                    queue.add(newNode)

    return bestTour
```

## Ant Colony Optimization (ACO)

ACO ist ein naturinspirierter Algorithmus, der das Verhalten von Ameisenkolonien bei der Futtersuche nachahmt und für große Probleminstanzen gut geeignet ist.

### Funktionsweise:

1. **Pheromonmodell**

   - Ablage von Pheromonen auf Wegen
   - Verdunstung über Zeit
   - Stärkung guter Pfade

2. **Tourenkonstruktion**

   - Probabilistische Stadtauswahl
   - Berücksichtigung von Distanzen
   - Nutzung von Pheromonspuren

3. **Aktualisierung**
   - Verdunstung alter Pheromone
   - Verstärkung guter Routen
   - Adaptive Anpassung

### Algorithmus:

```Pseudo
function antColonyOptimization(cities):
    initializePheromones()
    bestTour = null

    while !terminationCondition():
        antTours = []
        for ant in ants:
            tour = constructTour(cities)
            updatePheromones(tour)
            antTours.add(tour)

        evaporatePheromones()
        updateBestTour(antTours)

    return bestTour
```

## Modifizierter Nearest Neighbor mit Kostenzonenmodell

Der modifizierte Nearest Neighbor Algorithmus erweitert den klassischen Ansatz um ein Kostenzonenmodell, das reale geografische oder organisatorische Grenzen berücksichtigt.

### Funktionsweise:

1. **Zonenmodell**

   - Einteilung in Kostenzonen
   - Unterschiedliche Übergangskosten
   - Präferenz für Intrazonen-Bewegungen

2. **Kostenberechnung**

   - Distanzbasierte Grundkosten
   - Zonenfaktor-Multiplikator
   - Berücksichtigung von Grenzen

3. **Tourenkonstruktion**
   - Greedy-Auswahl nächster Städte
   - Zonenbasierte Kostenbewertung
   - Effiziente Routenfindung

### Algorithmus:

```Pseudo
function modifiedNearestNeighbor(cities, zones):
    currentCity = startCity
    tour = [currentCity]

    while unvisitedCities exist:
        nextCity = findCheapestNextCity(currentCity, zones)
        tour.add(nextCity)
        currentCity = nextCity

    tour.add(startCity)
    return tour

function findCheapestNextCity(current, zones):
    minCost = infinity
    bestCity = null

    for city in unvisitedCities:
        cost = distance(current, city) * zoneFactor(current, city)
        if cost < minCost:
            minCost = cost
            bestCity = city

    return bestCity
```

# 5. Softwarearchitektur <a name="architektur"></a>

### Use-Case Diagramm <a name="use-case"></a>

_Einfügen des UML Use-Case Diagramms mit Beschreibung_

### Klassendiagramm <a name="klassen"></a>

_Einfügen des UML Klassendiagramms mit Beschreibung der wichtigsten Komponenten_

# 6. Vergleich der Algorithmen <a name="vergleich"></a>

## Theoretischer Vergleich

Die drei implementierten Algorithmen unterscheiden sich grundlegend in ihren theoretischen Eigenschaften und Charakteristika:

| Kriterium                   | Branch & Bound       | ACO               | Mod. Nearest Neighbor |
| --------------------------- | -------------------- | ----------------- | --------------------- |
| Optimalität                 | ✓ Garantiert optimal | × Näherungslösung | × Näherungslösung     |
| Zeitkomplexität             | O(n!)                | O(n²m)            | O(n²)                 |
| Speicherbedarf              | Hoch                 | Mittel            | Niedrig               |
| Implementierungskomplexität | Komplex              | Mittel            | Einfach               |
| Skalierbarkeit              | Schlecht             | Gut               | Sehr gut              |
| Parallelisierbarkeit        | Begrenzt             | Sehr gut          | Begrenzt              |
| Adaptivität                 | Keine                | Hoch              | Keine                 |
| Deterministisch             | Ja                   | Nein              | Ja                    |

### Detaillierte Analyse der Eigenschaften

#### Branch & Bound

- **Vorteile**:
  - Garantiert die optimale Lösung
  - Deterministisches Verhalten
  - Beweisbare Korrektheit
- **Nachteile**:
  - Exponentieller Speicherbedarf
  - Praktisch nur für kleine Instanzen (n ≤ 20) geeignet
  - Hoher Implementierungsaufwand

#### Ant Colony Optimization (ACO)

- **Vorteile**:

  - Gute Balance zwischen Laufzeit und Lösungsqualität
  - Hervorragend parallelisierbar
  - Adaptiv an Problemänderungen

- **Nachteile**:
  - Keine Optimalitätsgarantie
  - Parameter müssen sorgfältig gewählt werden
  - Stochastisches Verhalten

#### Modifizierter Nearest Neighbor

- **Vorteile**:

  - Sehr schnelle Laufzeit
  - Einfache Implementierung
  - Geringer Speicherbedarf

- **Nachteile**:
  - Kann sehr suboptimale Lösungen produzieren
  - Keine Verbesserung durch längere Laufzeit
  - Anfällig für lokale Optima

## Praktischer Vergleich

[Bemerkung: Der praktische Vergleich wird nach der Implementierung und Durchführung von Testläufen mit verschiedenen Probleminstanzen ergänzt. Dabei werden folgende Aspekte untersucht:

- Laufzeitmessungen für verschiedene Problemgrößen
- Qualität der gefundenen Lösungen im Vergleich zum Optimum
- Speicherverbrauch in der Praxis
- Stabilität der Ergebnisse
- Praktische Einsetzbarkeit für verschiedene Anwendungsfälle]

# 7. Reflexion <a name="reflexion"></a>

Die Implementierung des TSP-Projekts führte zu wesentlichen Erkenntnissen in verschiedenen Bereichen:

### Technische Erkenntnisse

- Die praktische Implementierung von Branch and Bound zeigte deutlich die Grenzen exakter Verfahren bei wachsender Problemgröße
- JavaFX erwies sich als leistungsfähiges Framework für die Visualisierung der Algorithmen, benötigte jedoch sorgfältige Thread-Behandlung
- Die Parameterwahl bei ACO hat signifikanten Einfluss auf Laufzeit und Lösungsqualität

### Herausforderungen

- Effiziente Implementierung der Datenstrukturen für Branch and Bound, besonders bei der Verwaltung des Suchbaums
- Performance-Optimierung bei großen Datensätzen ohne Beeinträchtigung der GUI-Reaktivität
- Balance zwischen Laufzeitoptimierung und Codequalität/Wartbarkeit

### Verbesserungspotenzial

# 8. Quellenverzeichnis <a name="quellen"></a>

#### Grundlagen des TSP

1. Applegate, D., Bixby, R., Chvátal, V., & Cook, W. (2006). The Traveling Salesman Problem: A Computational Study. Princeton University Press.

   - Umfassende mathematische Grundlagen des TSP
   - Standardwerk für die exakte Problemformulierung
   - https://en.wikipedia.org/wiki/Travelling_salesman_problem (Zugriff am 12.12.2024)

2. Cook, W. (2012). In Pursuit of the Traveling Salesman: Mathematics at the Limits of Computation. Princeton University Press.
   - Historischer Kontext und Entwicklung des Problems
   - Aktuelle Forschungsansätze und Anwendungen
   - https://en.wikipedia.org/wiki/TSP (Zugriff am 16.12.2024)

#### Algorithmen und Implementierungen

3. Dorigo, M., & Stützle, T. (2019). Ant Colony Optimization: Overview and Recent Advances. In Handbook of Metaheuristics (pp. 311-351). Springer.

   - Detaillierte Beschreibung der ACO-Methodik
   - Aktuelle Entwicklungen und Varianten
   - DOI: 10.1007/978-3-319-91086-4_10
   - https://en.wikipedia.org/wiki/Ant_colony_optimization_algorithms (Zugriff am 16.12.2024)

4. Land, A. H., & Doig, A. G. (2010). An Automatic Method of Solving Discrete Programming Problems. In 50 Years of Integer Programming 1958-2008 (pp. 105-132). Springer.
   - Originalpublikation zum Branch-and-Bound-Verfahren
   - Theoretische Grundlagen und Beweise
   - https://en.wikipedia.org/wiki/K-nearest_neighbors_algorithm (Zugriff am 16.12.2024)
   - https://en.wikipedia.org/wiki/Branch_and_bound (Zugriff am 16.12.2024)

#### Online-Dokumentationen und Tutorials

5. Princeton University (2024). "Programming Assignment: TSP". CS Department.
   - Praktische Implementierungsbeispiele und Übungsaufgaben
   - https://www.cs.princeton.edu/courses/archive/spr15/cos126/assignments/tsp.html (Zugriff am 16.12.2024)

#### Technische Spezifikationen

6. Oracle (2024). "Java SE Technical Documentation".
   - Referenz für die Java-Implementierung
   - https://docs.oracle.com/en/java/
   - Zugriff am 16.12.2024

#### Ergänzende Ressourcen

7. European Union (2024). "TSPLIB - Library of Sample Instances for the TSP".
   - Standardisierte Testinstanzen und Benchmarks
   - http://comopt.ifi.uni-heidelberg.de/software/TSPLIB95/
   - Zugriff am 16.12.2024
