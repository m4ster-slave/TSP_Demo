package com.tsp.algorithm;

import com.tsp.gui.CityData.CityInfo;
import java.util.*;

public class NearestNeighbor implements TSPAlgorithm {
    private double pathLength;
    private long executionTime;

    @Override
    public List<CityInfo> findPath(List<CityInfo> cities) {
        long startTime = System.currentTimeMillis();
        
        // Placeholder implementation
        List<CityInfo> path = new ArrayList<>(cities);
        Collections.shuffle(path); // Random path for now
        
        pathLength = calculatePathLength(path);
        executionTime = System.currentTimeMillis() - startTime;
        return path;
    }

    @Override
    public String getName() {
        return "Nearest Neighbor";
    }

    @Override
    public double getPathLength() {
        return pathLength;
    }

    @Override
    public long getExecutionTime() {
        return executionTime;
    }

    private double calculatePathLength(List<CityInfo> path) {
        double length = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            length += calculateDistance(path.get(i), path.get(i + 1));
        }
        // Add distance back to start
        if (!path.isEmpty()) {
            length += calculateDistance(path.get(path.size() - 1), path.get(0));
        }
        return length;
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
}
