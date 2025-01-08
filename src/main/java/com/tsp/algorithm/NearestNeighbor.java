package com.tsp.algorithm;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.tsp.util.CityData.CityInfo;
import com.tsp.util.DistanceCalculator;
import com.tsp.util.ContinentsData;
import com.tsp.util.ContinentsData.ContinentInfo;

public class NearestNeighbor implements TSPAlgorithm {
  private double pathLength;
  private long executionTime;
  private ContinentsData continentsData = new ContinentsData("/data/continents.csv");

  // costs for different transitions
  private static final double SAME_COUNTRY_COST = 0.0;
  private static final double SAME_SUBREGION_COST = 1.0;
  private static final double SAME_CONTINENT_COST = 2.0;
  private static final double DIFFERENT_CONTINENT_COST = 3.0;

  @Override
  public List<CityInfo> findPath(List<CityInfo> cities) {
    long startTime = System.nanoTime();

    if (cities.isEmpty()) {
      return new ArrayList<>();
    }

    List<CityInfo> path = new ArrayList<>();
    Set<CityInfo> unvisitedCities = new HashSet<>(cities);

    // start with first city
    CityInfo currentCity = cities.get(0);
    path.add(currentCity);
    unvisitedCities.remove(currentCity);

    // find path until all cities are visited
    while (!unvisitedCities.isEmpty()) {
      CityInfo nextCity = findNearestCity(currentCity, unvisitedCities);
      path.add(nextCity);
      unvisitedCities.remove(nextCity);
      currentCity = nextCity;
    }

    // return to start
    path.add(cities.get(0));

    pathLength = DistanceCalculator.calculatePathLength(path);
    executionTime = System.nanoTime() - startTime;
    return path;
  }

  private CityInfo findNearestCity(CityInfo current, Set<CityInfo> unvisitedCities) {
    CityInfo nearest = null;
    double minCost = Double.MAX_VALUE;

    for (CityInfo candidate : unvisitedCities) {
      double distance = DistanceCalculator.calculateDistance(current, candidate);
      double transitionCost = calculateTransitionCost(current, candidate);
      double totalCost = distance + transitionCost;

      if (totalCost < minCost) {
        minCost = totalCost;
        nearest = candidate;
      }
    }

    return nearest;
  }

  private double calculateTransitionCost(CityInfo city1, CityInfo city2) {
    ContinentInfo info1 = continentsData.getContinentInfo(city1.getIso2Code());
    ContinentInfo info2 = continentsData.getContinentInfo(city2.getIso2Code());

    if (info1 == null || info2 == null) {
      return DIFFERENT_CONTINENT_COST;
    }

    // check hierarchy from specific to general
    if (info1.getCountry().equals(info2.getCountry())) {
      return SAME_COUNTRY_COST;
    }

    if (info1.getSubRegion().equals(info2.getSubRegion())) {
      return SAME_SUBREGION_COST;
    }

    if (info1.getContinent().equals(info2.getContinent())) {
      return SAME_CONTINENT_COST;
    }

    return DIFFERENT_CONTINENT_COST;
  }

  @Override
  public String getName() {
    return "Modified Nearest Neighbor";
  }

  @Override
  public double getPathLength() {
    return pathLength;
  }

  @Override
  public long getExecutionTime() {
    return executionTime;
  }
}
