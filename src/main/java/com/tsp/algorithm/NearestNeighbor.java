package com.tsp.algorithm;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.tsp.gui.CityData.CityInfo;
import com.tsp.util.ContinentsData;
import com.tsp.util.ContinentsData.ContinentInfo;

public class NearestNeighbor implements TSPAlgorithm {
    private double pathLength;
    private long executionTime;
    private final ContinentsData continentsData;
    
    // Kosten für verschiedene Übergänge
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
        
        // Startpunkt -> Erste Stadt
        CityInfo currentCity = cities.get(0);
        path.add(currentCity);
        unvisitedCities.remove(currentCity);
        
        // Findet Weg bis alle Städte besucht wurden
        while (!unvisitedCities.isEmpty()) {
            CityInfo nextCity = findNearestCity(currentCity, unvisitedCities);
            path.add(nextCity);
            unvisitedCities.remove(nextCity);
            currentCity = nextCity;
        }
        
        // Endpunkt -> Erste Stadt
        path.add(cities.get(0));
        
        pathLength = calculateTotalPathLength(path);
        executionTime = (System.nanoTime() - startTime) / 1000;
        return path;
    }

    private CityInfo findNearestCity(CityInfo current, Set<CityInfo> unvisitedCities) {
        CityInfo nearest = null;
        double minCost = Double.MAX_VALUE;
        
        for (CityInfo candidate : unvisitedCities) {
            double distance = calculateDistance(current, candidate);
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
            return DIFFERENT_CONTINENT_COST; // Fallback wenn keine Info verfügbar
        }
        
        // Prüfe Hierarchie von spezifisch nach allgemein
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

    private double calculateDistance(CityInfo city1, CityInfo city2) {
        double lat1 = Math.toRadians(city1.getLatitude());
        double lon1 = Math.toRadians(city1.getLongitude());
        double lat2 = Math.toRadians(city2.getLatitude());
        double lon2 = Math.toRadians(city2.getLongitude());

        // Haversine formula
        double dlon = lon2 - lon1;
        double dlat = lat2 - lat1;
        double a = Math.sin(dlat/2) * Math.sin(dlat/2) +
                   Math.cos(lat1) * Math.cos(lat2) *
                   Math.sin(dlon/2) * Math.sin(dlon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        
        // Earth's radius in kilometers
        double r = 6371;
        return c * r;
    }

    private double calculateTotalPathLength(List<CityInfo> path) {
        double total = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            CityInfo current = path.get(i);
            CityInfo next = path.get(i + 1);
            total += calculateDistance(current, next);
            total += calculateTransitionCost(current, next);
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