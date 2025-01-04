package com.tsp.gui;

import java.util.List;

import com.tsp.algorithm.ACO;
import com.tsp.algorithm.BranchAndBound;
import com.tsp.algorithm.NearestNeighbor;
import com.tsp.algorithm.TSPAlgorithm;
import com.tsp.gui.CityData.CityInfo;
import com.tsp.util.ContinentsData;

import javafx.geometry.Insets;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class AlgorithmPanel extends VBox {
    private final List<TSPAlgorithm> algorithms;
    private final TextArea resultArea;
    private final PathRenderer pathRenderer;
    private int selectedAlgorithm;
    
    public AlgorithmPanel(WorldMap worldMap) {
        setPadding(new Insets(10));
        setSpacing(10);
        
        ContinentsData continentsData = new ContinentsData();
        continentsData.loadFromCSV("/data/continents2.csv");

        algorithms = List.of(
            new NearestNeighbor(continentsData),
            new BranchAndBound(),
            new ACO()
        );
        
        pathRenderer = new PathRenderer(worldMap);
        worldMap.getChildren().add(pathRenderer);
        
        resultArea = new TextArea();
        resultArea.setEditable(false);
        resultArea.setPrefRowCount(11);
        
        getChildren().add(resultArea);
    }

    public void selectAlgorithm(int selectedAlgorithm) {
      this.selectedAlgorithm = selectedAlgorithm;
    }
    
    public void runAlgorithms(List<CityInfo> cities) {
        if (cities.size() < 2) {
            resultArea.setText("Add at least 2 cities to compare algorithms.");
            return;
        }
        
        StringBuilder results = new StringBuilder();
        
        for (TSPAlgorithm algorithm : algorithms) {
            List<CityInfo> path = algorithm.findPath(cities);
            results.append(String.format(
                "%s:\nPath Length: %.2f km\nTime: %d ms\n\n",
                algorithm.getName(),
                algorithm.getPathLength(),
                algorithm.getExecutionTime()
            ));
            
            if (algorithm == algorithms.get(selectedAlgorithm)) {
                pathRenderer.setPathColor(Color.BLUE);
                pathRenderer.renderPath(path);
            }
        }
        
        resultArea.setText(results.toString());
    }
    
    public void clear() {
        resultArea.clear();
        pathRenderer.clear();
    }
}
