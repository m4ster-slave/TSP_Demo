package com.tsp.algorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.tsp.util.CityData.CityInfo;
import com.tsp.util.DistanceCalculator;
import com.tsp.util.TSPUtils;

public class ACO implements TSPAlgorithm {
  private double pathLength;
  private long executionTime;

  private static final int NUM_ANTS = 30;
  private static final int MAX_ITERATIONS = 100;
  private static final double EVAPORATION_RATE = 0.15;
  private static final double ALPHA = 2.0; // pheromone importance
  private static final double BETA = 2.0; // distance importance
  private static final double Q = 100.0; // pheromone deposit factor

  @Override
  public List<CityInfo> findPath(List<CityInfo> cities) {
    long startTime = System.nanoTime();

    int numCities = cities.size();
    double[][] distances = TSPUtils.calculateDistanceMatrix(cities);
    double[][] pheromones = initializePheromones(numCities);
    List<CityInfo> bestPath = null;
    double bestLength = Double.MAX_VALUE;

    Random random = new Random();

    // main ACO loop
    for (int iteration = 0; iteration < MAX_ITERATIONS; iteration++) {
      // generate solutions for all ants
      for (int ant = 0; ant < NUM_ANTS; ant++) {
        List<CityInfo> currentPath = constructAntPath(cities, distances, pheromones, random);
        double currentLength = DistanceCalculator.calculatePathLength(currentPath);

        if (currentLength < bestLength) {
          bestLength = currentLength;
          bestPath = new ArrayList<>(currentPath);
        }
      }

      // update pheromones
      evaporatePheromones(pheromones);
      updatePheromones(pheromones, cities, bestPath, bestLength);
    }

    this.pathLength = bestLength;
    this.executionTime = System.nanoTime() - startTime;

    return bestPath;
  }

  private List<CityInfo> constructAntPath(List<CityInfo> cities,
      double[][] distances,
      double[][] pheromones,
      Random random) {
    int numCities = cities.size();
    List<CityInfo> path = new ArrayList<>();
    boolean[] visited = new boolean[numCities];

    // start from first city
    int currentCity = 0;
    path.add(cities.get(currentCity));
    visited[currentCity] = true;

    // path to remaining cities
    for (int i = 1; i < numCities; i++) {
      int nextCity = selectNextCity(currentCity, visited, pheromones, distances, random);
      path.add(cities.get(nextCity));
      visited[nextCity] = true;
      currentCity = nextCity;
    }

    // return to start
    path.add(cities.get(0));

    return path;
  }

  private int selectNextCity(int currentCity,
      boolean[] visited,
      double[][] pheromones,
      double[][] distances,
      Random random) {
    List<Integer> unvisitedCities = new ArrayList<>();
    List<Double> probabilities = new ArrayList<>();
    double total = 0.0;

    // calculate probabilities for each unvisited city
    int cityIndex = 0;
    for (boolean isVisited : visited) {
      if (!isVisited) {
        unvisitedCities.add(cityIndex);
        double probability = Math.pow(pheromones[currentCity][cityIndex], ALPHA) *
            Math.pow(1.0 / distances[currentCity][cityIndex], BETA);
        probabilities.add(probability);
        total += probability;
      }
      cityIndex++;
    }

    // select next city randomly based on probabilities
    double r = random.nextDouble() * total;
    double sum = 0.0;

    for (int i = 0; i < unvisitedCities.size(); i++) {
      sum += probabilities.get(i);
      if (sum >= r) {
        return unvisitedCities.get(i);
      }
    }

    // fallback: return first unvisited city
    return unvisitedCities.isEmpty() ? -1 : unvisitedCities.get(0);
  }

  private double[][] initializePheromones(int numCities) {
    double[][] pheromones = new double[numCities][numCities];
    double initialPheromone = 1.0;

    for (int i = 0; i < numCities; i++) {
      for (int j = 0; j < numCities; j++) {
        pheromones[i][j] = initialPheromone;
      }
    }

    return pheromones;
  }

  private void evaporatePheromones(double[][] pheromones) {
    for (int i = 0; i < pheromones.length; i++) {
      for (int j = 0; j < pheromones.length; j++) {
        pheromones[i][j] *= (1.0 - EVAPORATION_RATE);
      }
    }
  }

  private void updatePheromones(double[][] pheromones,
      List<CityInfo> cities,
      List<CityInfo> bestPath,
      double bestLength) {
    double deposit = Q / bestLength;

    for (int i = 0; i < bestPath.size() - 1; i++) {
      int cityIndex1 = cities.indexOf(bestPath.get(i));
      int cityIndex2 = cities.indexOf(bestPath.get(i + 1));
      pheromones[cityIndex1][cityIndex2] += deposit;
      pheromones[cityIndex2][cityIndex1] += deposit;
    }
  }

  @Override
  public String getName() {
    return "Ant Colony Optimization";
  }

  @Override
  public double getPathLength() {
    return this.pathLength;
  }

  @Override
  public long getExecutionTime() {
    return this.executionTime;
  }
}
