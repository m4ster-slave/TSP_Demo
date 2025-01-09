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

import javafx.application.Platform;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

public class AlgorithmPanel extends VBox {
  private final List<TSPAlgorithm> algorithms;
  private final TextArea resultArea;
  public final PathRenderer pathRenderer;
  private int selectedAlgorithm;
  private List<CityInfo> currentCities = new ArrayList<>();

  // cache for the algorithm results at their index
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
    algorithms = List.of(
        new NearestNeighbor(),
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
      // get the result for the selected algorithm
      CompletableFuture<AlgorithmResult> calculation = runningCalculations.get(index);
      // if algorithm result exists and is done
      if (calculation != null && calculation.isDone()) {
        try {
          AlgorithmResult result = calculation.get(); // get waits for future to complete
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

    // check if cities have changed
    boolean citiesChanged = !cities.equals(currentCities);
    currentCities = new ArrayList<>(cities);

    // if cities changef cancel existing calculations and start new ones
    if (citiesChanged) {
      runningCalculations.values().forEach(future -> future.cancel(true));
      runningCalculations.clear();

      resultArea.setText("Calculating...");

      // start new calculations for all algorithms
      for (int i = 0; i < algorithms.size(); i++) {
        final int algorithmIndex = i;
        final TSPAlgorithm algorithm = algorithms.get(i);

        // start a new thread to run the algorithm
        CompletableFuture<AlgorithmResult> calculation = CompletableFuture.supplyAsync(() -> {
          List<CityInfo> path = algorithm.findPath(cities);
          return new AlgorithmResult(
              path,
              algorithm.getPathLength(),
              algorithm.getExecutionTime());
        });

        // put the result of the calculation in the hashmap
        runningCalculations.put(algorithmIndex, calculation);

        // update UI when calculation completes
        calculation.thenAcceptAsync(result -> {
          if (algorithmIndex == selectedAlgorithm) {
            pathRenderer.renderPath(result.path);
          }
          updateResultArea();
        }, Platform::runLater);
      }
    } else {
      // if the cities dont have changed update the display for the selected algorithm
      CompletableFuture<AlgorithmResult> calculation = runningCalculations.get(selectedAlgorithm);
      if (calculation != null && calculation.isDone()) {
        try {
          AlgorithmResult result = calculation.get();
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

  private String formatPath(List<CityInfo> path) {
    if (path == null || path.isEmpty()) {
      return "No path";
    }
    return path.stream()
        .map(CityInfo::getName)
        .collect(java.util.stream.Collectors.joining(" -> "));
  }

  private void updateResultArea() {
    StringBuilder results = new StringBuilder();

    // for each algorithm set the text output
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

  public void clear() {
    resultArea.clear();
    pathRenderer.clear();
    runningCalculations.values().forEach(future -> future.cancel(true));
    runningCalculations.clear();
    currentCities.clear();
  }
}
