package com.tsp.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.tsp.algorithm.ACO;
import com.tsp.algorithm.BranchAndBound;
import com.tsp.algorithm.NearestNeighbor;
import com.tsp.algorithm.TSPAlgorithm;
import com.tsp.util.CityData.CityInfo;
import com.tsp.util.ContinentsData;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class AlgorithmPanel extends VBox {
  private final List<TSPAlgorithm> algorithms;
  private final TextArea resultArea;
  public final PathRenderer pathRenderer;
  private int selectedAlgorithm;
  private List<CityInfo> currentCities = new ArrayList<>();
  private final Map<Integer, CompletableFuture<AlgorithmResult>> runningCalculations = new HashMap<>();

  private static class AlgorithmResult {
    final List<CityInfo> path;
    final double pathLength;
    final long executionTime;

    AlgorithmResult(List<CityInfo> path, double pathLength, long executionTime) {
      this.path = path;
      this.pathLength = pathLength;
      this.executionTime = executionTime;
    }
  }

  public AlgorithmPanel(WorldMap worldMap) {
    setPadding(new Insets(10));
    setSpacing(10);

    ContinentsData continentsData = new ContinentsData();
    continentsData.loadFromCSV("/data/continents2.csv");

    algorithms = List.of(
        new NearestNeighbor(continentsData),
        new BranchAndBound(),
        new ACO());

    pathRenderer = new PathRenderer(worldMap);
    worldMap.getChildren().add(pathRenderer);

    resultArea = new TextArea();
    resultArea.setEditable(false);
    resultArea.setPrefRowCount(11);

    getChildren().add(resultArea);
  }

  public void selectAlgorithm(int index) {
    this.selectedAlgorithm = index;
    if (!currentCities.isEmpty()) {
      // Get the result for the selected algorithm
      CompletableFuture<AlgorithmResult> calculation = runningCalculations.get(index);
      if (calculation != null && calculation.isDone()) {
        try {
          AlgorithmResult result = calculation.get();
          pathRenderer.setPathColor(Color.BLUE);
          pathRenderer.renderPath(result.path);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
  }

  public void runAlgorithms(List<CityInfo> cities) {
    if (cities.size() < 2) {
      resultArea.setText("Add at least 2 cities to compare algorithms.");
      pathRenderer.clear();
      return;
    }

    // Check if cities have changed
    boolean citiesChanged = !cities.equals(currentCities);
    currentCities = new ArrayList<>(cities);

    // If cities changed, cancel existing calculations and start new ones
    if (citiesChanged) {
      // Cancel any running calculations
      runningCalculations.values().forEach(future -> future.cancel(true));
      runningCalculations.clear();

      resultArea.setText("Calculating...");

      // Start new calculations for all algorithms
      for (int i = 0; i < algorithms.size(); i++) {
        final int algorithmIndex = i;
        final TSPAlgorithm algorithm = algorithms.get(i);

        CompletableFuture<AlgorithmResult> calculation = CompletableFuture.supplyAsync(() -> {
          List<CityInfo> path = algorithm.findPath(cities);
          return new AlgorithmResult(
              path,
              algorithm.getPathLength(),
              algorithm.getExecutionTime());
        });

        runningCalculations.put(algorithmIndex, calculation);

        // Update UI when calculation completes
        calculation.thenAcceptAsync(result -> {
          if (algorithmIndex == selectedAlgorithm) {
            pathRenderer.setPathColor(Color.web("#3498d0"));
            pathRenderer.renderPath(result.path);
          }
          updateResultArea();
        }, Platform::runLater);
      }
    } else {
      // Just update the display for the selected algorithm
      CompletableFuture<AlgorithmResult> calculation = runningCalculations.get(selectedAlgorithm);
      if (calculation != null && calculation.isDone()) {
        try {
          AlgorithmResult result = calculation.get();
          pathRenderer.setPathColor(Color.web("#3498d0"));
          pathRenderer.renderPath(result.path);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
  }

  private String formatExecutionTime(long nanos) {
    if (nanos < 1_000_000) {
      return String.format("%d ns", nanos);
    }

    double ms = nanos / 1_000_000.0;
    if (ms < 1000) {
      return String.format("%.3f ms", ms);
    }

    double s = ms / 1000.0;
    return String.format("%.3f s", s);
  }

  private void updateResultArea() {
    StringBuilder results = new StringBuilder();

    for (int i = 0; i < algorithms.size(); i++) {
      CompletableFuture<AlgorithmResult> calculation = runningCalculations.get(i);
      if (calculation != null && calculation.isDone()) {
        try {
          AlgorithmResult result = calculation.get();
          results.append(String.format(
              "%s:\nPath Length: %.2f km\nTime: %s\nPath: %s\n\n",
              algorithms.get(i).getName(),
              result.pathLength,
              formatExecutionTime(result.executionTime),
              formatPath(result.path)));
        } catch (Exception e) {
          results.append(String.format("%s: Calculation failed\n\n",
              algorithms.get(i).getName()));
        }
      } else {
        results.append(String.format("%s: Calculating...\n\n",
            algorithms.get(i).getName()));
      }
    }

    resultArea.setText(results.toString());

  }

  private String formatPath(List<CityInfo> path) {
    if (path == null || path.isEmpty()) {
      return "No path";
    }
    return path.stream()
        .map(CityInfo::getName)
        .collect(java.util.stream.Collectors.joining(" → "));
  }

  public void clear() {
    resultArea.clear();
    pathRenderer.clear();
    runningCalculations.values().forEach(future -> future.cancel(true));
    runningCalculations.clear();
    currentCities.clear();
  }
}
