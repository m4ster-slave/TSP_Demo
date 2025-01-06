# Das Traveling Salesperson Problem

## Optimierung von Wegstrecken - Bucci Tobias & Lukas Weger

<div style="page-break-after: always;"></div>

# Problemstellung

- Bernadette muss Informationsblätter verteilen
- Kürzeste Route durch mehrere Klassenräume gesucht
- Start und Ende am gleichen Punkt

<div style="page-break-after: always;"></div>

# Analyse

- NP-vollständiges Problem
- Anzahl möglicher Routen: (n-1)!/2
- Beispiel: 15 Orte = 43,5 Mrd. Routen
- Balance zwischen Genauigkeit und Rechenzeit nötig

<div style="page-break-after: always;"></div>

# Implementierte Algorithmen

1. **Modifizierter Nearest Neighbor**

   - Berücksichtigt Kostenzonen
   - Schnelle Berechnung

2. **Branch and Bound**

   - Exakte Lösung
   - Für kleine Datensätze

3. **Ant Colony Optimization**
   - Naturinspiriert
   - Guter Kompromiss

<div style="page-break-after: always;"></div>

# Software

- JavaFX Benutzeroberfläche
- Weltkarte
- Städtesuche mit Autovervollständigung
- Echtzeitvergleich der Algorithmen

<div style="page-break-after: always;"></div>

# Vergleich der Algorithmen

| Kriterium   | Branch & Bound | ACO    | Nearest Neighbor |
| ----------- | -------------- | ------ | ---------------- |
| Optimalität | Garantiert     | Nein   | Nein             |
| Geschw.     | Langsam        | Mittel | Schnell          |
| Speicher    | Hoch           | Mittel | Niedrig          |

<div style="page-break-after: always;"></div>

# Demo

1. Städteauswahl
2. Routenberechnung
3. Algorithmenvergleich

<div style="page-break-after: always;"></div>

# Fazit

- Erfolgreiche Implementierung aller Algorithmen
- Praxistaugliche Lösung entwickelt
- Erweiterbar für andere Anwendungen

<div style="page-break-after: always;"></div>

# Fragen?

Vielen Dank für Ihre Aufmerksamkeit!
