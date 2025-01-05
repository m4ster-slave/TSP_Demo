package com.tsp.algorithm;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.tsp.gui.CityData.CityInfo;
import com.tsp.util.DistanceCalculator;
import com.tsp.util.ContinentsData;
import com.tsp.util.ContinentsData.ContinentInfo;

public class NearestNeighbor implements TSPAlgorithm {
    private double pathLength;
    private long executionTime;
    private final ContinentsData continentsData;
    
    // Costs for different transitions
    private static final double SAME_COUNTRY_COST = 0.0;  
    private static final double SAME_SUBREGION_COST = 1.0;
    private static final double SAME_CONTINENT_COST = 2.0;
    private static final double DIFFERENT_CONTINENT_COST = 3.0;

    public NearestNeighbor(ContinentsData continentsData) {
        this.continentsData = continentsData;
    }

    @Override
    public List<CityInfo> findPath(List<CityInfo> cities) {
        long startTime = System.nanoTime();
        
        if (cities.isEmpty()) {
            return new ArrayList<>();
        }

        List<CityInfo> path = new ArrayList<>();
        Set<CityInfo> unvisitedCities = new HashSet<>(cities);
        
        // Start with first city
        CityInfo currentCity = cities.get(0);
        path.add(currentCity);
        unvisitedCities.remove(currentCity);
        
        // Find path until all cities are visited
        while (!unvisitedCities.isEmpty()) {
            CityInfo nextCity = findNearestCity(currentCity, unvisitedCities);
            path.add(nextCity);
            unvisitedCities.remove(nextCity);
            currentCity = nextCity;
        }
        
        // Return to start
        path.add(cities.get(0));
        
        pathLength = calculateTotalPathLength(path);
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
        
        // Check hierarchy from specific to general
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

    private double calculateTotalPathLength(List<CityInfo> path) {
        if (path.isEmpty()) return 0;
        double total = DistanceCalculator.calculatePathLength(path);
        // Add transition costs
        for (int i = 0; i < path.size() - 1; i++) {
            total += calculateTransitionCost(path.get(i), path.get(i + 1));
        }
        return total;
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
